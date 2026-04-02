package com.library.backend.model.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Integer borrowId;
    private String bookId;
    private String studentId;
    private Integer score;
    private String content;
}