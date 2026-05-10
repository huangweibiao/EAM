package com.eam.repository;

import com.eam.entity.WorkOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 工单 Repository
 */
@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long>, JpaSpecificationExecutor<WorkOrder> {

    /**
     * 根据工单编号查询
     */
    Optional<WorkOrder> findByOrderNo(String orderNo);

    /**
     * 检查工单编号是否存在
     */
    boolean existsByOrderNo(String orderNo);

    /**
     * 根据资产ID查询工单
     */
    List<WorkOrder> findByAssetId(Long assetId);

    /**
     * 根据工单类型查询
     */
    List<WorkOrder> findByOrderType(String orderType);

    /**
     * 根据优先级查询
     */
    List<WorkOrder> findByPriority(String priority);

    /**
     * 根据状态查询
     */
    List<WorkOrder> findByStatus(String status);

    /**
     * 根据报告人查询
     */
    List<WorkOrder> findByReporter(String reporter);

    /**
     * 根据被指派人查询
     */
    List<WorkOrder> findByAssignedTo(String assignedTo);

    /**
     * 模糊查询工单标题
     */
    List<WorkOrder> findByTitleContaining(String keyword);

    /**
     * 多条件查询
     */
    @Query("SELECT o FROM WorkOrder o WHERE " +
           "(:orderNo IS NULL OR o.orderNo LIKE %:orderNo%) AND " +
           "(:assetId IS NULL OR o.assetId = :assetId) AND " +
           "(:orderType IS NULL OR o.orderType = :orderType) AND " +
           "(:priority IS NULL OR o.priority = :priority) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:reporter IS NULL OR o.reporter = :reporter) AND " +
           "(:assignedTo IS NULL OR o.assignedTo = :assignedTo) AND " +
           "(:title IS NULL OR o.title LIKE %:title%)")
    Page<WorkOrder> search(@Param("orderNo") String orderNo,
                          @Param("assetId") Long assetId,
                          @Param("orderType") String orderType,
                          @Param("priority") String priority,
                          @Param("status") String status,
                          @Param("reporter") String reporter,
                          @Param("assignedTo") String assignedTo,
                          @Param("title") String title,
                          Pageable pageable);
}