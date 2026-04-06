package com.eam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 采购申请表 Entity
 */
@Data
@TableName("purchase_request")
public class PurchaseRequest {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String requestNo;

    private Long partId;

    private BigDecimal requestQuantity;

    private BigDecimal estimatedPrice;

    private BigDecimal totalEstimatedAmount;

    private String urgency;

    private LocalDate expectedDate;

    private String reason;

    private String requester;

    private LocalDateTime requestTime;

    private String approver;

    private LocalDateTime approveTime;

    private String approveStatus;

    private String approveRemark;

    private Long purchaseOrderId;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}