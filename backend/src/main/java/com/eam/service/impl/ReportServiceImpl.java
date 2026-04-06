package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eam.entity.Asset;
import com.eam.entity.MaintenanceRecord;
import com.eam.entity.PartInbound;
import com.eam.entity.PartOutbound;
import com.eam.entity.SparePart;
import com.eam.mapper.AssetMapper;
import com.eam.mapper.MaintenanceRecordMapper;
import com.eam.mapper.PartInboundMapper;
import com.eam.mapper.PartOutboundMapper;
import com.eam.mapper.SparePartMapper;
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

    @Autowired
    private AssetMapper assetMapper;

    @Autowired
    private SparePartMapper sparePartMapper;

    @Autowired
    private MaintenanceRecordMapper maintenanceRecordMapper;

    @Autowired
    private PartInboundMapper inboundMapper;

    @Autowired
    private PartOutboundMapper outboundMapper;

    @Override
    public List<Map<String, Object>> assetSummaryByDept() {
        List<Asset> assets = assetMapper.selectList(null);
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
        List<Asset> assets = assetMapper.selectList(null);
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
        List<MaintenanceRecord> records = maintenanceRecordMapper.selectList(null);
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
        List<SparePart> parts = sparePartMapper.selectList(null);
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
        List<PartInbound> inbounds = inboundMapper.selectList(null);
        List<PartOutbound> outbounds = outboundMapper.selectList(null);

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