package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.MaintenanceRecord;
import com.eam.repository.MaintenanceRecordRepository;
import com.eam.service.IMaintenanceRecordService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
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

    private final MaintenanceRecordRepository recordRepository;

    public MaintenanceRecordServiceImpl(MaintenanceRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
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
        return recordRepository.save(record);
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
                    return date.getYear() + "-" + String.format("%02d", date.getMonthValue() + 1);
                }));

        // 计算总成本
        Map<String, Object> result = grouped.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            BigDecimal totalCost = entry.getValue().stream()
                                    .map(r -> r.getCost() != null ? r.getCost() : BigDecimal.ZERO)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            Map<String, Object> monthData = new java.util.HashMap<>();
                            monthData.put("count", entry.getValue().size());
                            monthData.put("totalCost", totalCost);
                            return monthData;
                        }
                ));

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