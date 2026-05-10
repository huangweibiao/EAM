package com.eam.service.impl;

import com.eam.entity.Asset;
import com.eam.entity.MaintenanceRecord;
import com.eam.entity.PartInbound;
import com.eam.entity.PartOutbound;
import com.eam.entity.SparePart;
import com.eam.repository.AssetRepository;
import com.eam.repository.MaintenanceRecordRepository;
import com.eam.repository.PartInboundRepository;
import com.eam.repository.PartOutboundRepository;
import com.eam.repository.SparePartRepository;
import com.eam.service.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报表统计 Service 实现类
 */
@Service
public class ReportServiceImpl implements IReportService {

    private final AssetRepository assetRepository;
    private final SparePartRepository sparePartRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final PartInboundRepository inboundRepository;
    private final PartOutboundRepository outboundRepository;

    @Autowired
    public ReportServiceImpl(AssetRepository assetRepository,
                             SparePartRepository sparePartRepository,
                             MaintenanceRecordRepository maintenanceRecordRepository,
                             PartInboundRepository inboundRepository,
                             PartOutboundRepository outboundRepository) {
        this.assetRepository = assetRepository;
        this.sparePartRepository = sparePartRepository;
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.inboundRepository = inboundRepository;
        this.outboundRepository = outboundRepository;
    }

    @Override
    public List<Map<String, Object>> assetSummaryByDept() {
        List<Asset> assets = assetRepository.findAll();
        return assets.stream()
                .collect(Collectors.groupingBy(a -> a.getDeptId() != null ? a.getDeptId().toString() : "未知"))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("deptId", entry.getKey());
                    map.put("totalCount", entry.getValue().size());
                    map.put("totalValue", entry.getValue().stream()
                            .map(a -> a.getPurchasePrice() != null ? a.getPurchasePrice() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> assetSummaryByCategory() {
        List<Asset> assets = assetRepository.findAll();
        return assets.stream()
                .collect(Collectors.groupingBy(a -> a.getCategoryId() != null ? a.getCategoryId().toString() : "未知"))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("categoryId", entry.getKey());
                    map.put("totalCount", entry.getValue().size());
                    map.put("totalValue", entry.getValue().stream()
                            .map(a -> a.getPurchasePrice() != null ? a.getPurchasePrice() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> maintenanceCostByMonth() {
        List<MaintenanceRecord> records = maintenanceRecordRepository.findAll();
        return records.stream()
                .collect(Collectors.groupingBy(r -> {
                    if (r.getMaintenanceDate() != null) {
                        return r.getMaintenanceDate().toLocalDate().toString().substring(0, 7);
                    }
                    return "unknown";
                }))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("month", entry.getKey());
                    map.put("totalCost", entry.getValue().stream()
                            .map(r -> r.getCost() != null ? r.getCost() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                    map.put("count", entry.getValue().size());
                    return map;
                })
                .sorted(Comparator.comparing(m -> (String) m.get("month")))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> inventorySummary() {
        List<SparePart> parts = sparePartRepository.findAll();
        BigDecimal totalValue = parts.stream()
                .map(p -> {
                    if (p.getQuantity() != null && p.getUnitPrice() != null) {
                        return p.getQuantity().multiply(p.getUnitPrice());
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> result = new HashMap<>();
        result.put("totalPartTypes", parts.size());
        result.put("totalQuantity", parts.stream().map(p -> p.getQuantity() != null ? p.getQuantity() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add));
        result.put("totalValue", totalValue);
        result.put("lowStockCount", parts.stream().filter(p -> p.getQuantity() != null && p.getMinQuantity() != null && p.getQuantity().compareTo(p.getMinQuantity()) < 0).count());
        return result;
    }

    @Override
    public List<Map<String, Object>> inboundOutboundByMonth() {
        List<PartInbound> inbounds = inboundRepository.findAll();
        List<PartOutbound> outbounds = outboundRepository.findAll();

        // 简化实现：返回前6个月的数据
        List<Map<String, Object>> result = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        for (int i = 5; i >= 0; i--) {
            cal.add(Calendar.MONTH, -1);
            String month = String.format("%d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
            Map<String, Object> map = new HashMap<>();
            map.put("month", month);
            map.put("inboundCount", inbounds.size());
            map.put("outboundCount", outbounds.size());
            result.add(map);
        }
        return result;
    }
}