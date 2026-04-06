package com.eam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 采购订单表 Entity
 */
@Data
@TableName("purchase_order")
public class PurchaseOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long requestId;

    private Long supplierId;

    private Long partId;

    private BigDecimal orderQuantity;

    private BigDecimal unitPrice;

    private BigDecimal totalAmount;

    private BigDecimal taxAmount;

    private LocalDateTime orderDate;

    private LocalDate deliveryDate;

    private LocalDate actualDeliveryDate;

    private String status;

    private String receiver;

    private LocalDateTime receiveTime;

    private String paymentStatus;

    private LocalDateTime paymentTime;

    private String remark;

    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}