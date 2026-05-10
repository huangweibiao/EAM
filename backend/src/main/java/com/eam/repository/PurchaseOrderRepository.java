package com.eam.repository;

import com.eam.entity.PurchaseOrder;
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
 * 采购订单 Repository
 */
@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long>, JpaSpecificationExecutor<PurchaseOrder> {

    /**
     * 根据订单编号查询
     */
    Optional<PurchaseOrder> findByOrderNo(String orderNo);

    /**
     * 检查订单编号是否存在
     */
    boolean existsByOrderNo(String orderNo);

    /**
     * 根据采购申请ID查询订单
     */
    List<PurchaseOrder> findByRequestId(Long requestId);

    /**
     * 根据供应商ID查询订单
     */
    List<PurchaseOrder> findBySupplierId(Long supplierId);

    /**
     * 根据备件ID查询订单
     */
    List<PurchaseOrder> findByPartId(Long partId);

    /**
     * 根据订单状态查询
     */
    List<PurchaseOrder> findByStatus(String status);

    /**
     * 根据付款状态查询
     */
    List<PurchaseOrder> findByPaymentStatus(String paymentStatus);

    /**
     * 根据接收人查询
     */
    List<PurchaseOrder> findByReceiver(String receiver);

    /**
     * 根据创建人查询
     */
    List<PurchaseOrder> findByCreateBy(String createBy);

    /**
     * 多条件查询
     */
    @Query("SELECT o FROM PurchaseOrder o WHERE " +
           "(:orderNo IS NULL OR o.orderNo LIKE %:orderNo%) AND " +
           "(:requestId IS NULL OR o.requestId = :requestId) AND " +
           "(:supplierId IS NULL OR o.supplierId = :supplierId) AND " +
           "(:partId IS NULL OR o.partId = :partId) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:paymentStatus IS NULL OR o.paymentStatus = :paymentStatus)")
    Page<PurchaseOrder> search(@Param("orderNo") String orderNo,
                              @Param("requestId") Long requestId,
                              @Param("supplierId") Long supplierId,
                              @Param("partId") Long partId,
                              @Param("status") String status,
                              @Param("paymentStatus") String paymentStatus,
                              Pageable pageable);
}