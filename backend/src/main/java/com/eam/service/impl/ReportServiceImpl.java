package com.eam.service.impl;

import com.eam.entity.Asset;
import com.eam.entity.MaintenanceRecord;
import com.eam.entity.PartInbound;
import com.eam.entity.PartOutbound;
import com.eam.entity.SparePart;
import com.eam.common.BusinessException;
import com.eam.repository.AssetRepository;
import com.eam.repository.MaintenanceRecordRepository;
import com.eam.repository.PartInboundRepository;
import com.eam.repository.PartOutboundRepository;
import com.eam.repository.SparePartRepository;
import com.eam.service.IReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报表服务实现
 */
@Service
public class ReportServiceImpl implements IReportService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);
    
    @Autowired
    private PartInboundRepository inboundRepository;
    
    @Autowired
    private PartOutboundRepository outboundRepository;
    
    @Autowired
    private AssetRepository assetRepository;
    
    @Autowired
    private MaintenanceRecordRepository maintenanceRecordRepository;
    
    @Autowired
    private SparePartRepository sparePartRepository;
    
    /**
     * 从日期获取月份字符串
     */
    private String getMonthFromDate(java.time.LocalDateTime date) {
        if (date == null) return "unknown";
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }
    
    /**
     * 按月份分组统计入库出库量
     */
    @Override
    public List<Map<String, Object>> inboundOutboundByMonth() {
        try {
            // 获取所有入库记录
            List<PartInbound> inbounds = inboundRepository.findAll();
            
            // 获取所有出库记录
            List<PartOutbound> outbounds = outboundRepository.findAll();
            
            // 按月份分组统计入库记录
            Map<String, Map<String, Object>> inboundByMonth = new HashMap<>();
            
            for (PartInbound inbound : inbounds) {
                String month = getMonthFromDate(inbound.getInboundDate());
                
                if (!inboundByMonth.containsKey(month)) {
                    inboundByMonth.put(month, new HashMap<>());
                }
                
                Map<String, Object> monthData = inboundByMonth.get(month);
                
                // 累计入库数量
                int inboundCount = (Integer) monthData.getOrDefault("inboundCount", 0);
                monthData.put("inboundCount", inboundCount + 1);
                
                // 累计入库总金额
                BigDecimal inboundTotalAmount = (BigDecimal) monthData.getOrDefault("inboundTotalAmount", BigDecimal.ZERO);
                if (inbound.getTotalAmount() != null) {
                    monthData.put("inboundTotalAmount", inboundTotalAmount.add(inbound.getTotalAmount()));
                }
                
                // 更新月份标识
                monthData.put("month", month);
            }
            
            // 按月份分组统计出库记录
            Map<String, Map<String, Object>> outboundByMonth = new HashMap<>();
            
            for (PartOutbound outbound : outbounds) {
                String month = getMonthFromDate(outbound.getOutboundDate());
                
                if (!outboundByMonth.containsKey(month)) {
                    outboundByMonth.put(month, new HashMap<>());
                }
                
                Map<String, Object> monthData = outboundByMonth.get(month);
                
                // 累计出库数量
                int outboundCount = (Integer) monthData.getOrDefault("outboundCount", 0);
                monthData.put("outboundCount", outboundCount + 1);
                
                // 累计出库总金额
                BigDecimal outboundTotalAmount = (BigDecimal) monthData.getOrDefault("outboundTotalAmount", BigDecimal.ZERO);
                if (outbound.getTotalAmount() != null) {
                    monthData.put("outboundTotalAmount", outboundTotalAmount.add(outbound.getTotalAmount()));
                }
                
                // 更新月份标识
                monthData.put("month", month);
            }
            
            // 合并按月份分组的统计结果
            List<Map<String, Object>> result = new ArrayList<>();
            
            // 获取所有月份的并集
            java.util.Set<String> allMonths = new java.util.LinkedHashSet<>();
            allMonths.addAll(inboundByMonth.keySet());
            allMonths.addAll(outboundByMonth.keySet());
            
            // 按月份排序
            List<String> sortedMonths = new ArrayList<>(allMonths);
            sortedMonths.sort((m1, m2) -> {
                String[] parts1 = m1.split("-");
                String[] parts2 = m2.split("-");
                int year1 = Integer.parseInt(parts1[0]);
                int year2 = Integer.parseInt(parts2[0]);
                int month1 = Integer.parseInt(parts1[1]);
                int month2 = Integer.parseInt(parts2[1]);
                
                if (year1 != year2) {
                    return year2 - year1;
                } else {
                    return month2 - month1;
                }
            });
            
            // 合并每个月的入库和出库数据
            for (String month : sortedMonths) {
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", month);
                
                // 入库数据
                if (inboundByMonth.containsKey(month)) {
                    Map<String, Object> inboundData = inboundByMonth.get(month);
                    monthData.put("inboundCount", inboundData.get("inboundCount"));
                    monthData.put("inboundTotalAmount", inboundData.get("inboundTotalAmount"));
                } else {
                    monthData.put("inboundCount", 0);
                    monthData.put("inboundTotalAmount", BigDecimal.ZERO);
                }
                
                // 出库数据
                if (outboundByMonth.containsKey(month)) {
                    Map<String, Object> outboundData = outboundByMonth.get(month);
                    monthData.put("outboundCount", outboundData.get("outboundCount"));
                    monthData.put("outboundTotalAmount", outboundData.get("outboundTotalAmount"));
                } else {
                    monthData.put("outboundCount", 0);
                    monthData.put("outboundTotalAmount", BigDecimal.ZERO);
                }
                
                // 计算净金额
                BigDecimal inboundAmount = (BigDecimal) monthData.getOrDefault("inboundTotalAmount", BigDecimal.ZERO);
                BigDecimal outboundAmount = (BigDecimal) monthData.getOrDefault("outboundTotalAmount", BigDecimal.ZERO);
                monthData.put("netAmount", inboundAmount.subtract(outboundAmount));
                
                result.add(monthData);
            }
            
            return result;
        } catch (Exception e) {
            logger.error("按月份统计入库出库量失败: {}", e.getMessage(), e);
            throw new BusinessException("按月份统计入库出库量失败: " + e.getMessage());
        }
    }

    /**
     * 按日期范围分组统计入库出库量
     */
    @Override
    public List<Map<String, Object>> inboundOutboundByGroupByMonth(String startDate,
                                                        String endDate) {
        try {
            // 解析日期
            java.time.LocalDate start = java.time.LocalDate.parse(startDate);
            java.time.LocalDate end = java.time.LocalDate.parse(endDate);
            
            // 获取指定日期范围内的入库记录
            List<PartInbound> inbounds = inboundRepository.findAll().stream()
                .filter(i -> i.getInboundDate() != null && 
                            !i.getInboundDate().toLocalDate().isBefore(start) &&
                            !i.getInboundDate().toLocalDate().isAfter(end))
                .collect(Collectors.toList());
            
            // 获取指定日期范围内的出库记录
            List<PartOutbound> outbounds = outboundRepository.findAll().stream()
                .filter(i -> i.getOutboundDate() != null && 
                            !i.getOutboundDate().toLocalDate().isBefore(start) &&
                            !i.getOutboundDate().toLocalDate().isAfter(end))
                .collect(Collectors.toList());
            
            // 按月份分组统计入库记录
            Map<String, Map<String, Object>> inboundByMonth = new HashMap<>();
            for (PartInbound inbound : inbounds) {
                String month = getMonthFromDate(inbound.getInboundDate());
                
                if (!inboundByMonth.containsKey(month)) {
                    inboundByMonth.put(month, new HashMap<>());
                }
                
                Map<String, Object> monthData = inboundByMonth.get(month);
                
                // 累计入库数量
                int inboundCount = (Integer) monthData.getOrDefault("inboundCount", 0);
                monthData.put("inboundCount", inboundCount + 1);
                
                // 累计入库总金额
                BigDecimal inboundTotalAmount = (BigDecimal) monthData.getOrDefault("inboundTotalAmount", BigDecimal.ZERO);
                if (inbound.getTotalAmount() != null) {
                    monthData.put("inboundTotalAmount", inboundTotalAmount.add(inbound.getTotalAmount()));
                }
                
                // 更新月份标识
                monthData.put("month", month);
            }

            // 按月份分组统计出库记录
            Map<String, Map<String, Object>> outboundByMonth = new HashMap<>();
            for (PartOutbound outbound : outbounds) {
                String month = getMonthFromDate(outbound.getOutboundDate());
                
                if (!outboundByMonth.containsKey(month)) {
                    outboundByMonth.put(month, new HashMap<>());
                }
                
                Map<String, Object> monthData = outboundByMonth.get(month);
                
                // 累计出库数量
                int outboundCount = (Integer) monthData.getOrDefault("outboundCount", 0);
                monthData.put("outboundCount", outboundCount + 1);
                
                // 累计出库总金额
                BigDecimal outboundTotalAmount = (BigDecimal) monthData.getOrDefault("outboundTotalAmount", BigDecimal.ZERO);
                if (outbound.getTotalAmount() != null) {
                    monthData.put("outboundTotalAmount", outboundTotalAmount.add(outbound.getTotalAmount()));
                }
                
                // 更新月份标识
                monthData.put("month", month);
            }

            // 合并按月份分组的统计结果
            List<Map<String, Object>> result = new ArrayList<>();
            
            // 获取所有月份的并集
            java.util.Set<String> allMonths = new java.util.LinkedHashSet<>();
            allMonths.addAll(inboundByMonth.keySet());
            allMonths.addAll(outboundByMonth.keySet());
            
            // 按月份排序
            List<String> sortedMonths = new ArrayList<>(allMonths);
            sortedMonths.sort((m1, m2) -> {
                String[] parts1 = m1.split("-");
                String[] parts2 = m2.split("-");
                int year1 = Integer.parseInt(parts1[0]);
                int year2 = Integer.parseInt(parts2[0]);
                int month1 = Integer.parseInt(parts1[1]);
                int month2 = Integer.parseInt(parts2[1]);
                
                if (year1 != year2) {
                    return year2 - year1;
                } else {
                    return month2 - month1;
                }
            });
            
            // 合并每个月的入库和出库数据
            for (String month : sortedMonths) {
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", month);
                monthData.put("startDate", startDate);
                monthData.put("endDate", endDate);
                
                // 入库数据
                if (inboundByMonth.containsKey(month)) {
                    Map<String, Object> inboundData = inboundByMonth.get(month);
                    monthData.put("inboundCount", inboundData.get("inboundCount"));
                    monthData.put("inboundTotalAmount", inboundData.get("inboundTotalAmount"));
                } else {
                    monthData.put("inboundCount", 0);
                    monthData.put("inboundTotalAmount", BigDecimal.ZERO);
                }
                
                // 出库数据
                if (outboundByMonth.containsKey(month)) {
                    Map<String, Object> outboundData = outboundByMonth.get(month);
                    monthData.put("outboundCount", outboundData.get("outboundCount"));
                    monthData.put("outboundTotalAmount", outboundData.get("outboundTotalAmount"));
                } else {
                    monthData.put("outboundCount", 0);
                    monthData.put("outboundTotalAmount", BigDecimal.ZERO);
                }
                
                // 计算净金额
                BigDecimal inboundAmount = (BigDecimal) monthData.getOrDefault("inboundTotalAmount", BigDecimal.ZERO);
                BigDecimal outboundAmount = (BigDecimal) monthData.getOrDefault("outboundTotalAmount", BigDecimal.ZERO);
                monthData.put("netAmount", inboundAmount.subtract(outboundAmount));
                
                result.add(monthData);
            }
            
            return result;
        } catch (Exception e) {
            logger.error("按日期范围分组统计入库出库量失败: {}", e.getMessage(), e);
            throw new BusinessException("按日期范围分组统计入库出库量失败: " + e.getMessage());
        }
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


}