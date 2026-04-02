package com.library.backend.model.vo;

import lombok.Data;
import java.util.Date;

@Data
public class ReviewVO {
    private Integer reviewId;
    private Integer borrowId;
    private String bookId;
    private String studentId;
    private Integer score;
    private String content;
    private Date reviewDate;

    // 多表联查字段
    private String bookTitle;
    private String studentName;
}