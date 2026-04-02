package com.library.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("review")
public class Review {
    @TableId(type = IdType.AUTO)
    private Integer reviewId;
    private Integer borrowId;
    private String bookId;
    private String studentId;
    private Integer score;
    private String content;
    private Date reviewDate;
}