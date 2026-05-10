package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.Asset;
import com.eam.entity.MaintenancePlan;
import com.eam.entity.MaintenanceRecord;
import com.eam.repository.MaintenancePlanRepository;
import com.eam.repository.MaintenanceRecordRepository;
import com.eam.service.IAssetService;
import com.eam.service.IMaintenanceRecordService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final IAssetService assetService;
    private final MaintenancePlanRepository maintenancePlanRepository;

    @Autowired
    public MaintenanceRecordServiceImpl(MaintenanceRecordRepository maintenanceRecordRepository,
                                         IAssetService assetService,
                                         MaintenancePlanRepository maintenancePlanRepository) {
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.assetService = assetService;
        this.maintenancePlanRepository = maintenancePlanRepository;
    }

    @Override
    public Page<MaintenanceRecord> page(Long pageNum, Long pageSize, Long assetId, String maintenanceType) {
        // JPA 分页从 0 开始，MyBatis-Plus 从 1 开始，需要转换
        Pageable pageable = PageRequest.of(pageNum.intValue() - 1, pageSize.intValue(),
                Sort.by(Sort.Direction.DESC, "maintenanceDate"));

        Specification<MaintenanceRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (assetId != null) {
                predicates.add(cb.equal(root.get("assetId"), assetId));
            }
            if (StringUtils.hasText(maintenanceType)) {
                predicates.add(cb.equal(root.get("maintenanceType"), maintenanceType));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return maintenanceRecordRepository.findAll(spec, pageable);
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

        maintenanceRecordRepository.save(record);

        // 更新资产的最后维护日期和下次维护日期
        updateAssetMaintenanceDates(record);

        // 更新维护计划的执行时间
        updateMaintenancePlan(record);

        return record;
    }

    @Override
    public MaintenanceRecord update(MaintenanceRecord record) {
        if (record.getId() == null) {
            throw new BusinessException("记录ID不能为空");
        }
        return maintenanceRecordRepository.save(record);
    }

    @Override
    public boolean delete(Long id) {
        maintenanceRecordRepository.deleteById(id);
        return true;
    }

    @Override
    public List<MaintenanceRecord> listByAsset(Long assetId) {
        return maintenanceRecordRepository.findByAssetIdOrderByMaintenanceDateDesc(assetId);
    }

    @Override
    public List<Map<String, Object>> statisticsByTime(String startDate, String endDate) {
        // 简单实现：按月份统计维护成本
        List<MaintenanceRecord> records = maintenanceRecordRepository.findAll();
        // 这里可以添加日期过滤逻辑

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
        List<MaintenanceRecord> records = maintenanceRecordRepository.findAll();
        return records.stream()
                .map(r -> r.getCost() != null ? r.getCost() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 更新资产的维护日期
     * 规则：资产下次维护日期 = 当前维护日期 + 维护周期（天）
     */
    private void updateAssetMaintenanceDates(MaintenanceRecord record) {
        if (record.getAssetId() == null || record.getMaintenanceDate() == null) {
            return;
        }

        Asset asset = assetService.getById(record.getAssetId());
        if (asset == null) {
            return;
        }

        java.time.LocalDate maintenanceDate = record.getMaintenanceDate().toLocalDate();
        java.time.LocalDate nextMaintenanceDate = null;

        // 如果记录中有建议的下次维护日期，使用它
        if (record.getNextMaintenanceDate() != null) {
            nextMaintenanceDate = record.getNextMaintenanceDate();
        } else if (asset.getMaintenanceCycle() != null && asset.getMaintenanceCycle() > 0) {
            // 否则根据维护周期计算：当前日期 + 维护周期（天）
            nextMaintenanceDate = maintenanceDate.plusDays(asset.getMaintenanceCycle());
        }

        // 更新资产的维护日期
        assetService.updateMaintenanceDates(record.getAssetId(), maintenanceDate, nextMaintenanceDate);
    }

    /**
     * 更新维护计划的执行时间
     */
    private void updateMaintenancePlan(MaintenanceRecord record) {
        if (record.getPlanId() == null) {
            return;
        }

        MaintenancePlan plan = maintenancePlanRepository.findById(record.getPlanId()).orElse(null);
        if (plan == null) {
            return;
        }

        // 更新上次执行时间
        plan.setLastExecuteTime(record.getMaintenanceDate());

        // 计算下次执行时间
        if (plan.getCycleType() != null && plan.getCycleValue() != null && plan.getCycleValue() > 0) {
            java.time.LocalDateTime nextExecuteTime = null;
            switch (plan.getCycleType().toUpperCase()) {
                case "DAY":
                    nextExecuteTime = record.getMaintenanceDate().plusDays(plan.getCycleValue());
                    break;
                case "MONTH":
                    nextExecuteTime = record.getMaintenanceDate().plusMonths(plan.getCycleValue());
                    break;
                case "YEAR":
                    nextExecuteTime = record.getMaintenanceDate().plusYears(plan.getCycleValue());
                    break;
                default:
                    nextExecuteTime = record.getMaintenanceDate().plusDays(plan.getCycleValue());
            }
            plan.setNextExecuteTime(nextExecuteTime);
        }

        maintenancePlanRepository.save(plan);
    }
}