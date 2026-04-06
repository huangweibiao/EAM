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
     * 出入库统计
     */
    List<Map<String, Object>> inboundOutboundByMonth();
}