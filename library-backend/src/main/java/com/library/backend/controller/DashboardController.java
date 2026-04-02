package com.library.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.backend.common.Result;
import com.library.backend.entity.Book;
import com.library.backend.entity.BorrowRecord;
import com.library.backend.entity.FineRecord;
import com.library.backend.model.vo.BorrowRecordVO;
import com.library.backend.service.BookService;
import com.library.backend.service.BorrowRecordService;
import com.library.backend.service.FineRecordService;
import com.library.backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {

    @Autowired private BookService bookService;
    @Autowired private StudentService studentService;
    @Autowired private BorrowRecordService borrowRecordService;
    @Autowired private FineRecordService fineRecordService;

    @GetMapping("/statistics")
    public Result getStatistics() {
        Map<String, Object> data = new HashMap<>();

        // 1. 基础数据统计
        List<Book> books = bookService.list();
        long totalBooks = books.stream().mapToInt(Book::getTotalCount).sum();
        long availableBooks = books.stream().mapToInt(Book::getAvailableCount).sum();
        long totalStudents = studentService.count();
        long totalBorrows = borrowRecordService.count(); // 累计借阅总人次

        data.put("totalBooks", totalBooks);
        data.put("totalStudents", totalStudents);
        data.put("totalBorrows", totalBorrows);

        // 2. 预警与待办统计
        long overdueCount = borrowRecordService.count(new QueryWrapper<BorrowRecord>().eq("status", "逾期").or().eq("status", "借出").apply("due_date < now()"));
        long unpaidFines = fineRecordService.count(new QueryWrapper<FineRecord>().eq("status", "未缴纳"));

        data.put("overdueAlerts", overdueCount);
        data.put("unpaidFines", unpaidFines);

        // 3. 图书状态分布 (计算百分比或原始数量)
        long borrowedBooks = totalBooks - availableBooks;
        Map<String, Long> bookStatus = new HashMap<>();
        bookStatus.put("available", availableBooks);
        bookStatus.put("borrowed", borrowedBooks);
        bookStatus.put("overdue", overdueCount);
        data.put("bookStatus", bookStatus);

        // 4. 最新借阅动态 (复用之前写好的分页联查方法，只取前5条)
        Page<BorrowRecordVO> page = borrowRecordService.getRecordPage(1, 5, null, null);
        List<Map<String, Object>> activities = new ArrayList<>();
        for (BorrowRecordVO vo : page.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("content", vo.getStudentName() + " 借阅了《" + vo.getBookTitle() + "》");
            map.put("timestamp", vo.getBorrowDate());
            map.put("status", vo.getStatus());
            activities.add(map);
        }
        data.put("activities", activities);

        return Result.success(data);
    }
}