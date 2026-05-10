package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.Asset;
import com.eam.entity.WorkOrder;
import com.eam.entity.WorkOrderPart;
import com.eam.repository.WorkOrderRepository;
import com.eam.service.IAssetService;
import com.eam.service.IWorkOrderService;
import com.eam.service.IWorkOrderPartService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 工单 Service 实现类
 */
@Service
public class WorkOrderServiceImpl implements IWorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final IWorkOrderPartService workOrderPartService;
    private final IAssetService assetService;

    public WorkOrderServiceImpl(WorkOrderRepository workOrderRepository, IWorkOrderPartService workOrderPartService, IAssetService assetService) {
        this.workOrderRepository = workOrderRepository;
        this.workOrderPartService = workOrderPartService;
        this.assetService = assetService;
    }

    @Override
    public Page<WorkOrder> page(Long pageNum, Long pageSize, String keyword, String status, String orderType) {
        // JPA 分页从 0 开始，MyBatis-Plus 从 1 开始，需要转换
        Pageable pageable = PageRequest.of(pageNum.intValue() - 1, pageSize.intValue(),
                Sort.by(Sort.Direction.DESC, "createTime"));

        Specification<WorkOrder> spec = (root, query, cb) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                predicates.add(cb.or(
                        cb.like(root.get("orderNo"), "%" + keyword + "%"),
                        cb.like(root.get("title"), "%" + keyword + "%")
                ));
            }
            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (StringUtils.hasText(orderType)) {
                predicates.add(cb.equal(root.get("orderType"), orderType));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return workOrderRepository.findAll(spec, pageable);
    }

    @Override
    public WorkOrder create(WorkOrder workOrder) {
        // 生成工单编号
        String orderNo = "WO" + System.currentTimeMillis();
        workOrder.setOrderNo(orderNo);
        if (workOrder.getStatus() == null) {
            workOrder.setStatus("PENDING");
        }
        if (workOrder.getPriority() == null) {
            workOrder.setPriority("MEDIUM");
        }
        if (workOrder.getReportTime() == null) {
            workOrder.setReportTime(LocalDateTime.now());
        }
        return workOrderRepository.save(workOrder);
    }

    @Override
    public WorkOrder assign(Long id, String assignedTo) {
        WorkOrder workOrder = workOrderRepository.findById(id).orElse(null);
        if (workOrder == null) {
            throw new BusinessException("工单不存在");
        }
        if (!"PENDING".equals(workOrder.getStatus())) {
            throw new BusinessException("只有待处理的工单可以指派");
        }
        workOrder.setAssignedTo(assignedTo);
        workOrder.setAssignTime(LocalDateTime.now());
        workOrder.setStatus("ASSIGNED");
        return workOrderRepository.save(workOrder);
    }

    @Override
    public WorkOrder process(Long id) {
        WorkOrder workOrder = workOrderRepository.findById(id).orElse(null);
        if (workOrder == null) {
            throw new BusinessException("工单不存在");
        }
        if (!"ASSIGNED".equals(workOrder.getStatus())) {
            throw new BusinessException("只有已指派的工单可以开始处理");
        }
        workOrder.setStatus("PROCESSING");
        return workOrderRepository.save(workOrder);
    }

    @Override
    @Transactional
    public WorkOrder complete(Long id, String solution) {
        WorkOrder workOrder = workOrderRepository.findById(id).orElse(null);
        if (workOrder == null) {
            throw new BusinessException("工单不存在");
        }
        if (!"PROCESSING".equals(workOrder.getStatus())) {
            throw new BusinessException("只有处理中的工单可以完成");
        }
        
        // 更新工单状态
        workOrder.setStatus("COMPLETED");
        workOrder.setSolution(solution);
        workOrder.setCompleteTime(LocalDateTime.now());
        workOrderRepository.save(workOrder);
        
        // 更新关联资产状态
        try {
            Asset asset = assetService.getById(workOrder.getAssetId());
            if (asset != null && "MAINTENANCE".equals(asset.getStatus())) {
                // 工单完成时，将资产状态从MAINTENANCE改为IN_USE
                asset.setStatus("IN_USE");
                assetService.update(asset);
            }
        } catch (Exception e) {
            // 记录错误但不影响工单完成
            System.err.println("更新资产状态失败: " + e.getMessage());
        }
        
        return workOrder;
    }

    @Override
    public WorkOrder close(Long id) {
        WorkOrder workOrder = workOrderRepository.findById(id).orElse(null);
        if (workOrder == null) {
            throw new BusinessException("工单不存在");
        }
        if (!"COMPLETED".equals(workOrder.getStatus())) {
            throw new BusinessException("只有已完成的工单可以关闭");
        }
        workOrder.setStatus("CLOSED");
        workOrder.setClosedTime(LocalDateTime.now());
        return workOrderRepository.save(workOrder);
    }

    @Override
    public WorkOrder rate(Long id, Integer rating) {
        WorkOrder workOrder = workOrderRepository.findById(id).orElse(null);
        if (workOrder == null) {
            throw new BusinessException("工单不存在");
        }
        if (!"COMPLETED".equals(workOrder.getStatus())) {
            throw new BusinessException("只有已完成的工单可以评价");
        }
        if (rating < 1 || rating > 5) {
            throw new BusinessException("评分范围1-5");
        }
        workOrder.setRating(rating);
        return workOrderRepository.save(workOrder);
    }

    @Override
    public WorkOrder getById(Long id) {
        return workOrderRepository.findById(id).orElse(null);
    }

    @Override
    public List<WorkOrder> list() {
        return workOrderRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
    }

    @Override
    @Transactional
    public WorkOrder createWithParts(WorkOrder workOrder, List<WorkOrderPart> parts) {
        // 保存工单
        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
        
        // 保存工单备件关联
        if (parts != null && !parts.isEmpty()) {
            for (WorkOrderPart part : parts) {
                part.setWorkOrderId(savedWorkOrder.getId());
            }
            workOrderPartService.addBatch(parts);
        }
        
        return savedWorkOrder;
    }

    @Override
    public WorkOrder addPart(WorkOrderPart workOrderPart) {
        return workOrderPartService.add(workOrderPart);
    }

    @Override
    @Transactional
    public WorkOrder cancel(Long id, String cancelReason, String cancelBy) {
        WorkOrder workOrder = workOrderRepository.findById(id).orElse(null);
        if (workOrder == null) {
            throw new BusinessException("工单不存在");
        }

        // 验证状态流转是否合法
        if (!isValidStatusTransition(workOrder.getStatus(), "CANCELED")) {
            throw new BusinessException("工单状态不允许取消操作。当前状态：" + workOrder.getStatus());
        }

        // 验证是否可以取消
        if ("CLOSED".equals(workOrder.getStatus()) || "CANCELED".equals(workOrder.getStatus())) {
            throw new BusinessException("工单已经" + workOrder.getStatus() + "，不能取消");
        }

        // 更新工单状态为取消
        workOrder.setStatus("CANCELED");
        
        // 记录取消信息
        if (cancelReason != null && !cancelReason.trim().isEmpty()) {
            workOrder.setSolution(cancelReason); // 使用solution字段记录取消原因
        }
        if (cancelBy != null && !cancelBy.trim().isEmpty()) {
            workOrder.setAssignedTo(cancelBy); // 使用assignedTo字段记录取消人
        }
        workOrder.setCancelTime(LocalDateTime.now());
        
        // 保存取消工单
        WorkOrder canceledWorkOrder = workOrderRepository.save(workOrder);
        
        // 取消工单后的关联数据清理
        try {
            // 清理工单的备件关联
            List<WorkOrderPart> parts = workOrderPartService.listByWorkOrderId(id);
            for (WorkOrderPart part : parts) {
                // 如果备件已出库，需要考虑库存处理
                // 这里简化处理，直接删除关联
                workOrderPartService.delete(part.getId());
            }
            
            // 如果工单关联资产且资产状态为MAINTENANCE，恢复为IN_USE状态
            if (workOrder.getAssetId() != null) {
                try {
                    Asset asset = assetService.getById(workOrder.getAssetId());
                    if (asset != null && "MAINTENANCE".equals(asset.getStatus())) {
                        asset.setStatus("IN_USE");
                        assetService.update(asset);
                    }
                } catch (Exception e) {
                    // 资产状态恢复异常，记录日志但不影响工单取消
                    System.err.println("恢复资产状态失败: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            // 关联数据清理异常，记录日志但不影响工单取消
            System.err.println("清理工单关联数据失败: " + e.getMessage());
        }
        
        return canceledWorkOrder;
    }

    @Override
    @Transactional
    public boolean updatePartConsumption(Long workOrderId, Long partId, BigDecimal actualQuantity) {
        // 查找工单备件关联
        List<WorkOrderPart> parts = workOrderPartService.listByWorkOrderId(workOrderId);
        for (WorkOrderPart part : parts) {
            if (part.getPartId().equals(partId)) {
                workOrderPartService.updateActualQuantity(part.getId(), actualQuantity);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removePartFromWorkOrder(Long workOrderId, Long partId) {
        return workOrderPartService.deleteByWorkOrderAndPart(workOrderId, partId);
    }

    @Override
    public boolean isValidStatusTransition(String currentStatus, String targetStatus) {
        if (currentStatus == null || targetStatus == null) {
            return false;
        }

        switch (currentStatus) {
            case "PENDING":
                // 待处理 -> 已指派、已取消
                return "ASSIGNED".equals(targetStatus) || "CANCELED".equals(targetStatus);
            case "ASSIGNED":
                // 已指派 -> 处理中、已取消
                return "PROCESSING".equals(targetStatus) || "CANCELED".equals(targetStatus);
            case "PROCESSING":
                // 处理中 -> 已完成、已取消
                return "COMPLETED".equals(targetStatus) || "CANCELED".equals(targetStatus);
            case "COMPLETED":
                // 已完成 -> 已关闭、已取消
                return "CLOSED".equals(targetStatus) || "CANCELED".equals(targetStatus);
            case "CLOSED":
            case "CANCELED":
                // 已关闭和已取消是最终状态
                return false;
            default:
                return false;
        }
    }

    @Override
    public List<WorkOrder> listPending() {
        return workOrderRepository.findByStatus("PENDING");
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (id == null) {
            return false;
        }
        
        try {
            // 检查工单是否存在
            WorkOrder workOrder = workOrderRepository.findById(id).orElse(null);
            if (workOrder == null) {
                return false;
            }
            
            // 根据业务规则决定是否可以删除
            // 例如：只有特定状态下可以删除，或者记录删除日志等
            
            // 执行删除
            workOrderRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            // 日志记录
            throw new BusinessException("删除工单失败: " + e.getMessage());
        }
    }
}