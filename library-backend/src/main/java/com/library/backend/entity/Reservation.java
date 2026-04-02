package com.library.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("reservation")
public class Reservation {
    @TableId(type = IdType.AUTO)
    private Integer reservationId;
    private String bookId;
    private String studentId;
    private Date reservationDate;
    private String status; // '等待','可取','取消','过期','已完成'
}