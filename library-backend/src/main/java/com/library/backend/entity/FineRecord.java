package com.library.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("fine_record")
public class FineRecord {
    @TableId(type = IdType.AUTO)
    private Integer fineId;

    private String studentId;
    private BigDecimal amount;
    private String reason;
    private String status; // '未缴纳', '已缴纳'
    private Date generateDate;
    private String handlerId;
}