package com.eam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 备件出库记录 Entity
 */
@Data
@TableName("part_outbound")
public class PartOutbound {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String outboundNo;

    private Long partId;

    private Long workOrderId;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalAmount;

    private LocalDateTime outboundDate;

    private Long departmentId;

    private String receiver;

    private String purpose;

    private String checker;

    private String remark;

    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}