package com.eam.service;

import com.eam.entity.WorkOrder;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 工单 Service 接口
 */
public interface IWorkOrderService {

    Page<WorkOrder> page(Long pageNum, Long pageSize, String keyword, String status, String orderType);

    WorkOrder create(WorkOrder workOrder);

    WorkOrder assign(Long id, String assignedTo);

    WorkOrder process(Long id);

    WorkOrder complete(Long id, String solution);

    WorkOrder close(Long id);

    WorkOrder getById(Long id);

    List<WorkOrder> list();

    WorkOrder rate(Long id, Integer rating);

    // 工单-备件关联相关方法

    /**
     * 创建工单时添加备件需求
     */
    WorkOrder createWithParts(WorkOrder workOrder, List<com.eam.entity.WorkOrderPart> parts);

    /**
     * 添加工单备件关联
     */
    com.eam.entity.WorkOrderPart addPart(com.eam.entity.WorkOrderPart workOrderPart);

    /**
     * 更新工单备件消耗量
     */
    boolean updatePartConsumption(Long workOrderId, Long partId, java.math.BigDecimal actualQuantity);

    /**
     * 获取工单的备件列表
     */
    List<com.eam.entity.WorkOrderPart> getPartsByWorkOrder(Long workOrderId);

    /**
     * 删除工单备件关联
     */
    boolean removePartFromWorkOrder(Long workOrderId, Long partId);

    /**
     * 取消工单
     * @param id 工单ID
     * @param cancelReason 取消原因
     * @param cancelBy 取消人
     */
    WorkOrder cancel(Long id, String cancelReason, String cancelBy);

    /**
     * 验证工单状态流转是否合法
     * @param currentStatus 当前状态
     * @param targetStatus 目标状态
     * @return 是否合法
     */
    boolean isValidStatusTransition(String currentStatus, String targetStatus);

    /**
     * 获取待处理的工单列表（状态为PENDING）
     * @return 待处理工单列表
     */
    List<WorkOrder> listPending();

    /**
     * 删除工单
     * @param id 工单ID
     * @return 是否删除成功
     */
    boolean delete(Long id);
}