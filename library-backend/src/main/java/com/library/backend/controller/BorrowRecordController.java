package com.library.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.backend.common.Result;
import com.library.backend.model.dto.BorrowDTO;
import com.library.backend.model.vo.BorrowRecordVO;
import com.library.backend.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrow")
@CrossOrigin
public class BorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    /**
     * 读者发起借书操作
     */
    @PostMapping("/do")
    public Result borrowBook(@RequestBody BorrowDTO borrowDTO) {
        try {
            borrowRecordService.borrowBook(borrowDTO);
            return Result.success("借阅成功");
        } catch (RuntimeException e) {
            // 业务异常（如超出额度、库存不足、账号冻结）
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "系统繁忙，借阅失败");
        }
    }

    /**
     * 获取借阅流水记录（供管理员后台查看）
     */
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String status) {
        Page<BorrowRecordVO> page = borrowRecordService.getRecordPage(current, size, keyword, status);
        return Result.success(page);
    }

    /**
     * 管理员操作：确认还书
     */
    @PostMapping("/return/{id}")
    public Result returnBook(@PathVariable Integer id) {
        try {
            borrowRecordService.returnBook(id);
            return Result.success("还书处理成功");
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "系统繁忙，还书失败");
        }
    }
}