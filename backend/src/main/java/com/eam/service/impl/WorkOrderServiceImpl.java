package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eam.common.BusinessException;
import com.eam.entity.WorkOrder;
import com.eam.mapper.WorkOrderMapper;
import com.eam.service.IWorkOrderService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 工单 Service 实现类
 */
@Service
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements IWorkOrderService {

    @Override
    public IPage<WorkOrder> page(Long pageNum, Long pageSize, String keyword, String status, String orderType) {
        Page<WorkOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<WorkOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(WorkOrder::getOrderNo, keyword)
                    .or().like(WorkOrder::getTitle, keyword));
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(WorkOrder::getStatus, status);
        }
        if (StringUtils.hasText(orderType)) {
            wrapper.eq(WorkOrder::getOrderType, orderType);
        }
        wrapper.orderByDesc(WorkOrder::getCreateTime);
        return this.page(page, wrapper);
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
        this.save(workOrder);
        return workOrder;
    }

    @Override
    public WorkOrder assign(Long id, String assignedTo) {
        WorkOrder workOrder = this.getById(id);
        if (workOrder == null) {
            throw new BusinessException("工单不存在");
        }
        if (!"PENDING".equals(workOrder.getStatus())) {
            throw new BusinessException("只有待处理的工单可以指派");
        }
        workOrder.setAssignedTo(assignedTo);
        workOrder.setAssignTime(LocalDateTime.now());
        workOrder.setStatus("ASSIGNED");
        this.updateById(workOrder);
        return workOrder;
    }

    @Override
    public WorkOrder process(Long id) {
        WorkOrder workOrder = this.getById(id);
        if (workOrder == null) {
            throw new BusinessException("工单不存在");
        }
        if (!"ASSIGNED".equals(workOrder.getStatus())) {
            throw new BusinessException("只有已指派的工单可以开始处理");
        }
        workOrder.setStatus("PROCESSING");
        this.updateById(workOrder);
        return workOrder;
    }

    @Override
    public WorkOrder complete(Long id, String solution) {
        WorkOrder workOrder = this.getById(id);
        if (workOrder == null) {
            throw new BusinessException("工单不存在");
        }
        if (!"PROCESSING".equals(workOrder.getStatus())) {
            throw new BusinessException("只有处理中的工单可以完成");
        }
        workOrder.setStatus("COMPLETED");
        workOrder.setSolution(solution);
        workOrder.setCompleteTime(LocalDateTime.now());
        this.updateById(workOrder);
        return workOrder;
    }

    @Override
    public WorkOrder close(Long id) {
        WorkOrder workOrder = this.getById(id);
        if (workOrder == null) {
            throw new BusinessException("工单不存在");
        }
        if (!"COMPLETED".equals(workOrder.getStatus())) {
            throw new BusinessException("只有已完成的工单可以关闭");
        }
        workOrder.setStatus("CLOSED");
        workOrder.setClosedTime(LocalDateTime.now());
        this.updateById(workOrder);
        return workOrder;
    }

    @Override
    public WorkOrder rate(Long id, Integer rating) {
        WorkOrder workOrder = this.getById(id);
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
        this.updateById(workOrder);
        return workOrder;
    }
}