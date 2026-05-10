package com.eam.service;

import java.util.List;
import java.util.Map;

/**
 * 报表统计 Service 接口
 */
public interface IReportService {

    /**
     * 资产汇总统计(按部门/分类)
     */
    List<Map<String, Object>> assetSummaryByDept();

    List<Map<String, Object>> assetSummaryByCategory();

    /**
     * 维护成本统计
     */
    List<Map<String, Object>> maintenanceCostByMonth();

    /**
     * 库存汇总统计
     */
    Map<String, Object> inventorySummary();

    /**
     * 按月份分组统计入库出库量
     * Task 13.2.1: 使用GROUP BY实现按月统计
     */
    List<Map<String, Object>> inboundOutboundByGroupByMonth();

    /**
     * 按月份分组统计入库量
     */
    List<Map<String, Object>> inboundOutboundByGroupByMonth(@RequestParam String startDate,
                                                      @RequestParam String endDate);

    /**
     * 按月份分组统计出库量
     */
    List<Map<String, Object>> inboundOutboundByGroupByMonth(@RequestParam String startDate,
                                                      @RequestParam String endDate);
}