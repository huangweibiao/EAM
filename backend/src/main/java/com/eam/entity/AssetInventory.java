package com.eam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 资产盘点 Entity
 */
@Entity
@Table(name = "eam_asset_inventory")
public class AssetInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inventory_no")
    private String inventoryNo;

    @Column(name = "inventory_name")
    private String inventoryName;

    @Column(name = "dept_id")
    private Long deptId;

    @Column(name = "inventory_scope")
    private String inventoryScope;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    private String status;

    @Column(name = "total_asset_count")
    private Integer totalAssetCount;

    @Column(name = "actual_count")
    private Integer actualCount;

    @Column(name = "mismatch_count")
    private Integer mismatchCount;

    @Column(name = "create_by")
    private String createBy;

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

    public String getInventoryNo() {
        return inventoryNo;
    }

    public void setInventoryNo(String inventoryNo) {
        this.inventoryNo = inventoryNo;
    }

    public String getInventoryName() {
        return inventoryName;
    }

    public void setInventoryName(String inventoryName) {
        this.inventoryName = inventoryName;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getInventoryScope() {
        return inventoryScope;
    }

    public void setInventoryScope(String inventoryScope) {
        this.inventoryScope = inventoryScope;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalAssetCount() {
        return totalAssetCount;
    }

    public void setTotalAssetCount(Integer totalAssetCount) {
        this.totalAssetCount = totalAssetCount;
    }

    public Integer getActualCount() {
        return actualCount;
    }

    public void setActualCount(Integer actualCount) {
        this.actualCount = actualCount;
    }

    public Integer getMismatchCount() {
        return mismatchCount;
    }

    public void setMismatchCount(Integer mismatchCount) {
        this.mismatchCount = mismatchCount;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
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