package com.eam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 资产主表 Entity
 */
@Data
@TableName("asset")
public class Asset {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String assetCode;

    private String assetName;

    private Long categoryId;

    private String model;

    private String brand;

    private String serialNumber;

    private Long deptId;

    private Long userId;

    private String location;

    private LocalDate purchaseDate;

    private BigDecimal purchasePrice;

    private BigDecimal currentValue;

    private Long supplierId;

    private LocalDate warrantyEndDate;

    private String status;

    private Integer maintenanceCycle;

    private LocalDate lastMaintenanceDate;

    private LocalDate nextMaintenanceDate;

    private String qrCode;

    private String remark;

    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}