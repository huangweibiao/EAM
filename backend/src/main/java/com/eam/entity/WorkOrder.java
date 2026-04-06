package com.eam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单 Entity
 */
@Data
@TableName("work_order")
public class WorkOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long assetId;

    private String orderType;

    private String priority;

    private String title;

    private String description;

    private String reporter;

    private LocalDateTime reportTime;

    private String assignedTo;

    private LocalDateTime assignTime;

    private String status;

    private String solution;

    private LocalDateTime completeTime;

    private LocalDateTime closedTime;

    private Integer rating;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}