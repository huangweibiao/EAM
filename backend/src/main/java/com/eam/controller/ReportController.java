package com.eam.controller;

import com.eam.common.Result;
import com.eam.service.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 报表分析 Controller
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private IReportService reportService;

    /**
     * 资产汇总统计-按部门
     */
    @GetMapping("/asset-summary/dept")
    public Result<?> assetSummaryByDept() {
        return Result.success(reportService.assetSummaryByDept());
    }

    /**
     * 资产汇总统计-按分类
     */
    @GetMapping("/asset-summary/category")
    public Result<?> assetSummaryByCategory() {
        return Result.success(reportService.assetSummaryByCategory());
    }

    /**
     * 维护成本统计
     */
    @GetMapping("/maintenance-cost")
    public Result<?> maintenanceCost() {
        return Result.success(reportService.maintenanceCostByMonth());
    }

    /**
     * 库存汇总统计
     */
    @GetMapping("/inventory-summary")
    public Result<?> inventorySummary() {
        return Result.success(reportService.inventorySummary());
    }

    /**
     * 出入库统计
     */
    @GetMapping("/inbound-outbound")
    public Result<?> inboundOutbound() {
        return Result.success(reportService.inboundOutboundByMonth());
    }
}