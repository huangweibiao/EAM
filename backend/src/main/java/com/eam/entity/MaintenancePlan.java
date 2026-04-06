package com.eam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 维护计划 Entity
 */
@Data
@TableName("maintenance_plan")
public class MaintenancePlan {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String planCode;

    private Long assetId;

    private String planName;

    private String maintenanceType;

    private String cycleType;

    private Integer cycleValue;

    private LocalDateTime lastExecuteTime;

    private LocalDateTime nextExecuteTime;

    private String status;

    private String responsiblePerson;

    private String remark;

    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}