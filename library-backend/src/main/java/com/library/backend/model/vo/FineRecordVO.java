package com.library.backend.model.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class FineRecordVO {
    private Integer fineId;
    private String studentId;
    private BigDecimal amount;
    private String reason;
    private String status;
    private Date generateDate;
    private String handlerId;

    // 联表多出的字段
    private String studentName;
    private String handlerName; // 处理罚单的管理员姓名
}