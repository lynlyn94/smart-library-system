package com.library.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("borrow_record")
public class BorrowRecord {

    // 【核心修复】明确映射数据库的 borrow_id 字段
    @TableId(value = "borrow_id", type = IdType.AUTO)
    private Integer borrowId;

    private String bookId;
    private String studentId;
    private Date borrowDate;
    private Date dueDate;
    private Date returnDate;
    private String status;
}