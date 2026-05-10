package com.eam.service;

import com.eam.entity.WorkOrderPart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 工单-备件关联 Service 接口
 */
public interface IWorkOrderPartService {

    /**
     * 分页查询工单备件关联
     */
    Page<WorkOrderPart> page(Long workOrderId, Pageable pageable);

    /**
     * 根据工单ID查询备件列表
     */
    List<WorkOrderPart> listByWorkOrderId(Long workOrderId);

    /**
     * 根据备件ID查询工单列表
     */
    List<WorkOrderPart> listByPartId(Long partId);

    /**
     * 添加工单备件关联
     */
    WorkOrderPart add(WorkOrderPart workOrderPart);

    /**
     * 更新工单备件关联
     */
    WorkOrderPart update(WorkOrderPart workOrderPart);

    /**
     * 删除工单备件关联
     */
    boolean delete(Long id);

    /**
     * 根据工单ID和备件ID删除关联
     */
    boolean deleteByWorkOrderAndPart(Long workOrderId, Long partId);

    /**
     * 更新实际消耗数量
     */
    WorkOrderPart updateActualQuantity(Long id, java.math.BigDecimal actualQuantity);

    /**
     * 批量添加工单备件关联
     */
    List<WorkOrderPart> addBatch(List<WorkOrderPart> workOrderParts);
}