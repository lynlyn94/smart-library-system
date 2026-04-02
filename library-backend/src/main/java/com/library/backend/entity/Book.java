package com.library.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("book")
public class Book {

    // 主键：ISBN，必须手动输入
    @TableId(type = IdType.INPUT)
    private String bookId;

    private String title;
    private String author;
    private String publisher;
    private LocalDate publishDate; // 出版日期
    private BigDecimal price;      // 价格，用 BigDecimal 更精确
    private String category;
    private String location;
    private Integer totalCount;
    private Integer availableCount;
    private String status;         // '正常','遗失','损坏','下架'
    private String description;
    private String coverUrl;
}