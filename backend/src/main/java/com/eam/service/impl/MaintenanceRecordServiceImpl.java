package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.Asset;
import com.eam.entity.MaintenanceRecord;
import com.eam.repository.MaintenanceRecordRepository;
import com.eam.service.IAssetService;
import com.eam.service.IMaintenanceRecordService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 维护记录 Service 实现类
 */
@Service
public class MaintenanceRecordServiceImpl implements IMaintenanceRecordService {

    private final MaintenanceRecordRepository recordRepository;
    private final IAssetService assetService;

    public MaintenanceRecordServiceImpl(MaintenanceRecordRepository recordRepository, IAssetService assetService) {
        this.recordRepository = recordRepository;
        this.assetService = assetService;
    }

    @Override
    public Page<MaintenanceRecord> page(Long pageNum, Long pageSize, Long assetId, String maintenanceType) {
        Specification<MaintenanceRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (assetId != null) {
                predicates.add(cb.equal(root.get("assetId"), assetId));
            }
            if (maintenanceType != null && !maintenanceType.isEmpty()) {
                predicates.add(cb.equal(root.get("maintenanceType"), maintenanceType));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(pageNum.intValue() - 1, pageSize.intValue(),
                Sort.by(Sort.Direction.DESC, "maintenanceDate"));
        return recordRepository.findAll(spec, pageable);
    }

    @Override
    public MaintenanceRecord add(MaintenanceRecord record) {
        if (record.getMaintenanceDate() == null) {
            record.setMaintenanceDate(LocalDateTime.now());
        }
        
        // 保存维护记录
        MaintenanceRecord savedRecord = recordRepository.save(record);
        
        // 获取关联资产并更新维护日期
        try {
            Asset asset = assetService.getById(record.getAssetId());
            if (asset != null && asset.getMaintenanceCycle() != null && asset.getMaintenanceCycle() > 0) {
                // 计算下次维护日期 = 当前维护日期 + 维护周期（天）
                LocalDate lastMaintenanceDate = record.getMaintenanceDate().toLocalDate();
                LocalDate nextMaintenanceDate = lastMaintenanceDate.plusDays(asset.getMaintenanceCycle());
                
                // 更新资产维护日期
                assetService.updateMaintenanceDates(
                    record.getAssetId(),
                    lastMaintenanceDate,
                    nextMaintenanceDate
                );
            }
        } catch (Exception e) {
            // 记录错误但不影响维护记录的保存
            System.err.println("更新资产维护日期失败: " + e.getMessage());
        }
        
        return savedRecord;
    }

    @Override
    public MaintenanceRecord update(MaintenanceRecord record) {
        if (record.getId() == null) {
            throw new BusinessException("维护记录ID不能为空");
        }
        return recordRepository.save(record);
    }

    @Override
    public boolean delete(Long id) {
        recordRepository.deleteById(id);
        return true;
    }

    @Override
    public List<MaintenanceRecord> listByAsset(Long assetId) {
        return recordRepository.findByAssetIdOrderByMaintenanceDateDesc(assetId);
    }

    @Override
    public List<Map<String, Object>> statisticsByTime(String startDate, String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);

        List<MaintenanceRecord> records = recordRepository.findByMaintenanceDateBetween(start, end);

        // 按月份分组统计
        Map<String, List<MaintenanceRecord>> grouped = records.stream()
                .collect(Collectors.groupingBy(record -> {
                    LocalDateTime date = record.getMaintenanceDate();
                    return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
                }));

        // 计算总成本
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<MaintenanceRecord>> entry : grouped.entrySet()) {
            BigDecimal totalCost = entry.getValue().stream()
                    .map(r -> r.getCost() != null ? r.getCost() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Map<String, Object> monthData = new java.util.HashMap<>();
            monthData.put("month", entry.getKey());
            monthData.put("count", entry.getValue().size());
            monthData.put("totalCost", totalCost);
            result.add(monthData);
        }

        return result;
    }

    @Override
    public BigDecimal getTotalCost() {
        List<MaintenanceRecord> records = recordRepository.findAll();
        return records.stream()
                .map(r -> r.getCost() != null ? r.getCost() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<MaintenanceRecord> list() {
        return recordRepository.findAll();
    }

    @Override
    public MaintenanceRecord getById(Long id) {
        return recordRepository.findById(id).orElse(null);
    }
}
