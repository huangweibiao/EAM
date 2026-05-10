package com.eam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 资产盘点明细 Entity
 */
@Entity
@Table(name = "eam_asset_inventory_detail")
public class AssetInventoryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inventory_id")
    private Long inventoryId;

    @Column(name = "asset_id")
    private Long assetId;

    @Column(name = "system_location")
    private String systemLocation;

    @Column(name = "actual_location")
    private String actualLocation;

    @Column(name = "system_status")
    private String systemStatus;

    @Column(name = "actual_status")
    private String actualStatus;

    @Column(name = "is_match")
    private Integer isMatch;

    @Column(name = "inventory_time")
    private LocalDateTime inventoryTime;

    @Column(name = "inventory_by")
    private String inventoryBy;

    private String remark;

    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public String getSystemLocation() {
        return systemLocation;
    }

    public void setSystemLocation(String systemLocation) {
        this.systemLocation = systemLocation;
    }

    public String getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(String actualLocation) {
        this.actualLocation = actualLocation;
    }

    public String getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(String systemStatus) {
        this.systemStatus = systemStatus;
    }

    public String getActualStatus() {
        return actualStatus;
    }

    public void setActualStatus(String actualStatus) {
        this.actualStatus = actualStatus;
    }

    public Integer getIsMatch() {
        return isMatch;
    }

    public void setIsMatch(Integer isMatch) {
        this.isMatch = isMatch;
    }

    public LocalDateTime getInventoryTime() {
        return inventoryTime;
    }

    public void setInventoryTime(LocalDateTime inventoryTime) {
        this.inventoryTime = inventoryTime;
    }

    public String getInventoryBy() {
        return inventoryBy;
    }

    public void setInventoryBy(String inventoryBy) {
        this.inventoryBy = inventoryBy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}