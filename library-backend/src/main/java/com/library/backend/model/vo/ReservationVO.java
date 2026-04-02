package com.library.backend.model.vo;

import lombok.Data;
import java.util.Date;

@Data
public class ReservationVO {
    private Integer reservationId;
    private String bookId;
    private String studentId;
    private Date reservationDate;
    private String status;

    // 多表联查字段
    private String bookTitle;
    private String studentName;
}