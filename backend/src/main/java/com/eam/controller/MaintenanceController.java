package com.eam.controller;

import org.springframework.data.domain.Page;
import com.eam.security.RequirePermission;
import com.eam.common.PageResult;
import com.eam.common.Result;
import com.eam.entity.MaintenancePlan;
import com.eam.entity.MaintenanceRecord;
import com.eam.service.IMaintenancePlanService;
import com.eam.service.IMaintenanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 设备维护 Controller
 */
@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    @Autowired
    private IMaintenancePlanService planService;

    @Autowired
    private IMaintenanceRecordService recordService;

    // ========== 维护计划 ==========

    /**
     * 分页查询维护计划
     */
    @GetMapping("/plan/page")
    @RequirePermission("maintenance:plan:list")
    public Result<PageResult<MaintenancePlan>> planPage(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long assetId,
            @RequestParam(required = false) String status) {
        Page<MaintenancePlan> page = planService.page(pageNum, pageSize, assetId, status);
        PageResult<MaintenancePlan> result = PageResult.of(
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getContent()
        );
        return Result.success(result);
    }

    /**
     * 获取所有维护计划
     */
    @GetMapping("/plan/list")
    @RequirePermission("maintenance:plan:list")
    public Result<?> planList() {
        return Result.success(planService.list());
    }

    /**
     * 根据ID获取维护计划
     */
    @GetMapping("/plan/{id}")
    @RequirePermission("maintenance:plan:list")
    public Result<MaintenancePlan> getPlanById(@PathVariable Long id) {
        return Result.success(planService.getById(id));
    }

    /**
     * 新增维护计划
     */
    @PostMapping("/plan/add")
    @RequirePermission("maintenance:plan:add")
    public Result<MaintenancePlan> addPlan(@RequestBody MaintenancePlan plan) {
        return Result.success(planService.add(plan));
    }

    /**
     * 修改维护计划
     */
    @PutMapping("/plan/update")
    @RequirePermission("maintenance:plan:update")
    public Result<MaintenancePlan> updatePlan(@RequestBody MaintenancePlan plan) {
        return Result.success(planService.update(plan));
    }

    /**
     * 删除维护计划
     */
    @DeleteMapping("/plan/{id}")
    @RequirePermission("maintenance:plan:delete")
    public Result<?> deletePlan(@PathVariable Long id) {
        return Result.success(planService.delete(id));
    }

    /**
     * 获取待执行的维护计划
     */
    @GetMapping("/plan/pending")
    @RequirePermission("maintenance:plan:list")
    public Result<?> planPending() {
        return Result.success(planService.listPending());
    }

    /**
     * 获取即将到期的维护计划
     */
    @GetMapping("/plan/remind")
    @RequirePermission("maintenance:plan:list")
    public Result<?> planRemind(@RequestParam(defaultValue = "7") int days) {
        return Result.success(planService.listExpiring(days));
    }

    // ========== 维护记录 ==========

    /**
     * 分页查询维护记录
     */
    @GetMapping("/record/page")
    @RequirePermission("maintenance:record:list")
    public Result<PageResult<MaintenanceRecord>> recordPage(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long assetId,
            @RequestParam(required = false) String maintenanceType) {
        Page<MaintenanceRecord> page = recordService.page(pageNum, pageSize, assetId, maintenanceType);
        PageResult<MaintenanceRecord> result = PageResult.of(
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getContent()
        );
        return Result.success(result);
    }

    /**
     * 获取所有维护记录
     */
    @GetMapping("/record/list")
    @RequirePermission("maintenance:record:list")
    public Result<?> recordList() {
        return Result.success(recordService.list());
    }

    /**
     * 根据ID获取维护记录
     */
    @GetMapping("/record/{id}")
    @RequirePermission("maintenance:record:list")
    public Result<MaintenanceRecord> getRecordById(@PathVariable Long id) {
        return Result.success(recordService.getById(id));
    }

    /**
     * 新增维护记录
     */
    @PostMapping("/record/add")
    @RequirePermission("maintenance:record:add")
    public Result<MaintenanceRecord> addRecord(@RequestBody MaintenanceRecord record) {
        return Result.success(recordService.add(record));
    }

    /**
     * 修改维护记录
     */
    @PutMapping("/record/update")
    @RequirePermission("maintenance:record:update")
    public Result<MaintenanceRecord> updateRecord(@RequestBody MaintenanceRecord record) {
        return Result.success(recordService.update(record));
    }

    /**
     * 删除维护记录
     */
    @DeleteMapping("/record/{id}")
    @RequirePermission("maintenance:record:delete")
    public Result<?> deleteRecord(@PathVariable Long id) {
        return Result.success(recordService.delete(id));
    }

    /**
     * 获取资产的维护记录
     */
    @GetMapping("/record/asset/{assetId}")
    @RequirePermission("maintenance:record:list")
    public Result<?> recordByAsset(@PathVariable Long assetId) {
        return Result.success(recordService.listByAsset(assetId));
    }

    /**
     * 维护成本统计
     */
    @GetMapping("/record/cost")
    @RequirePermission("maintenance:report:cost")
    public Result<?> costStatistics(@RequestParam String startDate, @RequestParam String endDate) {
        return Result.success(recordService.statisticsByTime(startDate, endDate));
    }

    /**
     * 获取总维护成本
     */
    @GetMapping("/record/totalCost")
    @RequirePermission("maintenance:report:cost")
    public Result<?> totalCost() {
        return Result.success(recordService.getTotalCost());
    }
}