package com.eam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资产变动记录 Entity
 */
@Data
@TableName("asset_change_log")
public class AssetChangeLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long assetId;

    private String changeType;

    private String oldValue;

    private String newValue;

    private String reason;

    private LocalDateTime changeTime;

    private String operator;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}