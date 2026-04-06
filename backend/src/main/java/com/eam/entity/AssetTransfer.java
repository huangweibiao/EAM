package com.eam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资产调拨 Entity
 */
@Data
@TableName("asset_transfer")
public class AssetTransfer {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String transferNo;

    private Long assetId;

    private Long fromDeptId;

    private Long toDeptId;

    private Long fromUserId;

    private Long toUserId;

    private String transferReason;

    private LocalDateTime transferTime;

    private String operator;

    private String approver;

    private LocalDateTime approveTime;

    private String status;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}