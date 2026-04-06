package com.eam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资产盘点 Entity
 */
@Data
@TableName("asset_inventory")
public class AssetInventory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String inventoryNo;

    private String inventoryName;

    private Long deptId;

    private String inventoryScope;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status;

    private Integer totalAssetCount;

    private Integer actualCount;

    private Integer mismatchCount;

    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}