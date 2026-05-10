package com.eam.repository;

import com.eam.entity.WorkOrderPart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 工单备件关联 Repository
 */
@Repository
public interface WorkOrderPartRepository extends JpaRepository<WorkOrderPart, Long>, JpaSpecificationExecutor<WorkOrderPart> {

    /**
     * 根据工单ID分页查询备件列表
     */
    Page<WorkOrderPart> findByWorkOrderId(Long workOrderId, Pageable pageable);

    /**
     * 根据工单ID查询备件列表
     */
    List<WorkOrderPart> findByWorkOrderId(Long workOrderId);

    /**
     * 根据备件ID查询工单列表
     */
    List<WorkOrderPart> findByPartId(Long partId);

    /**
     * 根据工单ID和备件ID查询
     */
    WorkOrderPart findByWorkOrderIdAndPartId(Long workOrderId, Long partId);

    /**
     * 根据工单ID和备件ID删除关联
     */
    void deleteByWorkOrderIdAndPartId(Long workOrderId, Long partId);

    /**
     * 根据出库记录ID查询
     */
    List<WorkOrderPart> findByOutboundId(Long outboundId);

    /**
     * 检查工单备件关联是否存在
     */
    boolean existsByWorkOrderIdAndPartId(Long workOrderId, Long partId);
}