package com.eam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eam.entity.MaintenanceRecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 维护记录 Service 接口
 */
public interface IMaintenanceRecordService extends IService<MaintenanceRecord> {

    IPage<MaintenanceRecord> page(Long pageNum, Long pageSize, Long assetId, String maintenanceType);

    MaintenanceRecord add(MaintenanceRecord record);

    MaintenanceRecord update(MaintenanceRecord record);

    boolean delete(Long id);

    List<MaintenanceRecord> listByAsset(Long assetId);

    /**
     * 统计维护成本
     */
    List<Map<String, Object>> statisticsByTime(String startDate, String endDate);

    /**
     * 获取总维护成本
     */
    BigDecimal getTotalCost();
}