package com.library.backend.model.dto;

import lombok.Data;

@Data
public class BorrowDTO {
    // 借书人的学号
    private String studentId;

    // 👉 【核心修复】这里必须是 String 类型！绝不能是 Integer！
    private String bookId;
}