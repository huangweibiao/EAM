package com.eam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 工单-备件关联 Entity
 */
@Data
@TableName("work_order_part")
public class WorkOrderPart {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long workOrderId;

    private Long partId;

    private BigDecimal planQuantity;

    private BigDecimal actualQuantity;

    private Long outboundId;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}