package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eam.common.BusinessException;
import com.eam.entity.MaintenanceRecord;
import com.eam.mapper.MaintenanceRecordMapper;
import com.eam.service.IMaintenanceRecordService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 维护记录 Service 实现类
 */
@Service
public class MaintenanceRecordServiceImpl extends ServiceImpl<MaintenanceRecordMapper, MaintenanceRecord> implements IMaintenanceRecordService {

    @Override
    public IPage<MaintenanceRecord> page(Long pageNum, Long pageSize, Long assetId, String maintenanceType) {
        Page<MaintenanceRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MaintenanceRecord> wrapper = new LambdaQueryWrapper<>();
        if (assetId != null) {
            wrapper.eq(MaintenanceRecord::getAssetId, assetId);
        }
        if (StringUtils.hasText(maintenanceType)) {
            wrapper.eq(MaintenanceRecord::getMaintenanceType, maintenanceType);
        }
        wrapper.orderByDesc(MaintenanceRecord::getMaintenanceDate);
        return this.page(page, wrapper);
    }

    @Override
    public MaintenanceRecord add(MaintenanceRecord record) {
        // 生成维护单号
        String maintenanceCode = "MR" + System.currentTimeMillis();
        record.setMaintenanceCode(maintenanceCode);
        if (record.getMaintenanceDate() == null) {
            record.setMaintenanceDate(LocalDateTime.now());
        }
        if (record.getResult() == null) {
            record.setResult("COMPLETED");
        }
        if (record.getCreateTime() == null) {
            record.setCreateTime(LocalDateTime.now());
        }

        this.save(record);

        // 更新资产的最后维护日期和下次维护日期
        // 这里可以调用AssetService来更新

        return record;
    }

    @Override
    public MaintenanceRecord update(MaintenanceRecord record) {
        if (record.getId() == null) {
            throw new BusinessException("记录ID不能为空");
        }
        this.updateById(record);
        return record;
    }

    @Override
    public boolean delete(Long id) {
        return this.removeById(id);
    }

    @Override
    public List<MaintenanceRecord> listByAsset(Long assetId) {
        return this.list(new LambdaQueryWrapper<MaintenanceRecord>()
                .eq(MaintenanceRecord::getAssetId, assetId)
                .orderByDesc(MaintenanceRecord::getMaintenanceDate));
    }

    @Override
    public List<Map<String, Object>> statisticsByTime(String startDate, String endDate) {
        // 简单实现：按月份统计维护成本
        List<MaintenanceRecord> records = this.list(
                new LambdaQueryWrapper<MaintenanceRecord>()
                        .ge(MaintenanceRecord::getMaintenanceDate, startDate)
                        .le(MaintenanceRecord::getMaintenanceDate, endDate)
        );

        return records.stream()
                .collect(Collectors.groupingBy(r -> {
                    if (r.getMaintenanceDate() != null) {
                        return r.getMaintenanceDate().toLocalDate().toString().substring(0, 7);
                    }
                    return "unknown";
                }))
                .entrySet().stream()
                .map(entry -> {
                    BigDecimal totalCost = entry.getValue().stream()
                            .map(r -> r.getCost() != null ? r.getCost() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("month", entry.getKey());
                    map.put("totalCost", totalCost);
                    map.put("count", entry.getValue().size());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTotalCost() {
        List<MaintenanceRecord> records = this.list();
        return records.stream()
                .map(r -> r.getCost() != null ? r.getCost() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}