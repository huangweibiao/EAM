package com.eam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 维护执行记录 Entity
 */
@Data
@TableName("maintenance_record")
public class MaintenanceRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long planId;

    private Long assetId;

    private String maintenanceCode;

    private LocalDateTime maintenanceDate;

    private String maintenanceType;

    private String content;

    private BigDecimal cost;

    private String technician;

    private String result;

    private LocalDate nextMaintenanceDate;

    private String attachments;

    private String remark;

    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}