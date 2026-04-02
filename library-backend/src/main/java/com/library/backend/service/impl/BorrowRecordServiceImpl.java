package com.library.backend.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.backend.entity.Book;
import com.library.backend.entity.BorrowRecord;
import com.library.backend.entity.Student;
import com.library.backend.mapper.BorrowRecordMapper;
import com.library.backend.model.dto.BorrowDTO;
import com.library.backend.model.vo.BorrowRecordVO;
import com.library.backend.service.BookService;
import com.library.backend.service.BorrowRecordService;
import com.library.backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Service
public class BorrowRecordServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements BorrowRecordService {

    @Autowired
    private BookService bookService;

    @Autowired
    private StudentService studentService;

    /**
     * 执行借书事务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void borrowBook(BorrowDTO borrowDTO) {
        // 1. 查询读者并校验
        Student student = studentService.getById(borrowDTO.getStudentId());
        if (student == null) throw new RuntimeException("找不到该读者信息");
        if (!"正常".equals(student.getAccountStatus())) throw new RuntimeException("账号处于" + student.getAccountStatus() + "状态，无法借书");
        if (student.getCurrentBorrow() >= student.getMaxBorrow()) throw new RuntimeException("借阅数量已达上限 (" + student.getMaxBorrow() + "本)");

        // 2. 查询图书并校验
        Book book = bookService.getById(borrowDTO.getBookId());
        if (book == null) throw new RuntimeException("找不到该图书信息");
        if (book.getAvailableCount() <= 0) throw new RuntimeException("该图书已被借空，暂无库存");

        // 3. 扣减图书可借库存
        book.setAvailableCount(book.getAvailableCount() - 1);
        bookService.updateById(book);

        // 4. 增加学生当前借阅量
        student.setCurrentBorrow(student.getCurrentBorrow() + 1);
        studentService.updateById(student);

        // 5. 生成借阅流水记录
        BorrowRecord record = new BorrowRecord();
        record.setStudentId(student.getStudentId());
        record.setBookId(book.getBookId());
        record.setBorrowDate(new Date());

        // 默认借阅期限为 30 天
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        record.setDueDate(calendar.getTime());

        // 【核心修改】状态必须严格匹配数据库的 ENUM 中文值
        record.setStatus("借出");
        this.save(record);
    }

    /**
     * 获取借阅流水（带多表联查）
     */
    @Override
    public Page<BorrowRecordVO> getRecordPage(Integer current, Integer size, String keyword, String status) {
        return this.baseMapper.selectRecordPage(new Page<>(current, size), keyword, status);
    }

    /**
     * 执行还书事务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(Integer recordId) {
        // 1. 查询借阅记录并校验
        BorrowRecord record = this.getById(recordId);
        if (record == null) throw new RuntimeException("找不到该借阅流水");

        // 【核心修改】检查是否已经为中文的"已还"状态
        if ("已还".equals(record.getStatus())) throw new RuntimeException("该书已归还，请勿重复操作");

        // 2. 更新流水状态为“已还”，并记录实际还书时间
        record.setStatus("已还");
        record.setReturnDate(new Date());
        this.updateById(record);

        // 3. 归还图书，可借库存 +1
        Book book = bookService.getById(record.getBookId());
        if (book != null) {
            book.setAvailableCount(book.getAvailableCount() + 1);
            bookService.updateById(book);
        }

        // 4. 学生当前借阅数 -1 (释放借阅额度)
        Student student = studentService.getById(record.getStudentId());
        if (student != null && student.getCurrentBorrow() > 0) {
            student.setCurrentBorrow(student.getCurrentBorrow() - 1);
            studentService.updateById(student);
        }
    }
}