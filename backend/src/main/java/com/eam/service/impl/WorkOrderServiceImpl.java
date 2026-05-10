package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.WorkOrder;
import com.eam.repository.WorkOrderRepository;
import com.eam.service.IWorkOrderService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 工单 Service 实现类
 */
@Service
public class WorkOrderServiceImpl implements IWorkOrderService {

    private final WorkOrderRepository workOrderRepository;

    public WorkOrderServiceImpl(WorkOrderRepository workOrderRepository) {
        this.workOrderRepository = workOrderRepository;
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
    public WorkOrder complete(Long id, String solution) {
        WorkOrder workOrder = workOrderRepository.findById(id).orElse(null);
        if (workOrder == null) {
            throw new BusinessException("工单不存在");
        }
        if (!"PROCESSING".equals(workOrder.getStatus())) {
            throw new BusinessException("只有处理中的工单可以完成");
        }
        workOrder.setStatus("COMPLETED");
        workOrder.setSolution(solution);
        workOrder.setCompleteTime(LocalDateTime.now());
        return workOrderRepository.save(workOrder);
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
}