package com.eam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资产盘点明细 Entity
 */
@Data
@TableName("asset_inventory_detail")
public class AssetInventoryDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long inventoryId;

    private Long assetId;

    private String systemLocation;

    private String actualLocation;

    private String systemStatus;

    private String actualStatus;

    private Integer isMatch;

    private LocalDateTime inventoryTime;

    private String inventoryBy;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}