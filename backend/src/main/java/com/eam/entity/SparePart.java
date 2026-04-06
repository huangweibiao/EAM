package com.eam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 备件表 Entity
 */
@Data
@TableName("spare_part")
public class SparePart {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String partCode;

    private String partName;

    private Long categoryId;

    private String model;

    private String unit;

    private BigDecimal quantity;

    private BigDecimal minQuantity;

    private BigDecimal maxQuantity;

    private String location;

    private Long supplierId;

    private BigDecimal unitPrice;

    private String status;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}