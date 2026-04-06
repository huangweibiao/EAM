package com.eam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 备件入库记录 Entity
 */
@Data
@TableName("part_inbound")
public class PartInbound {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String inboundNo;

    private Long partId;

    private Long purchaseOrderId;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalAmount;

    private LocalDateTime inboundDate;

    private Long supplierId;

    private String batchNo;

    private LocalDate productionDate;

    private LocalDate expiryDate;

    private String checker;

    private String remark;

    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}