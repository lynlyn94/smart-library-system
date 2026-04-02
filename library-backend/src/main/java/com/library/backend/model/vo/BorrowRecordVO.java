package com.library.backend.model.vo;

import lombok.Data;
import java.util.Date;

@Data
public class BorrowRecordVO {
    // 【核心修复】改为 borrowId
    private Integer borrowId;

    private String bookId;
    private String studentId;
    private Date borrowDate;
    private Date dueDate;
    private Date returnDate;
    private String status;

    private String bookTitle;
    private String studentName;
}