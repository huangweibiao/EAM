package com.eam.repository;

import com.eam.entity.PurchaseRequest;
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
 * 采购申请 Repository
 */
@Repository
public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long>, JpaSpecificationExecutor<PurchaseRequest> {

    /**
     * 根据申请编号查询
     */
    Optional<PurchaseRequest> findByRequestNo(String requestNo);

    /**
     * 检查申请编号是否存在
     */
    boolean existsByRequestNo(String requestNo);

    /**
     * 根据备件ID查询采购申请
     */
    List<PurchaseRequest> findByPartId(Long partId);

    /**
     * 根据紧急程度查询
     */
    List<PurchaseRequest> findByUrgency(String urgency);

    /**
     * 根据申请人查询
     */
    List<PurchaseRequest> findByRequester(String requester);

    /**
     * 根据审批人查询
     */
    List<PurchaseRequest> findByApprover(String approver);

    /**
     * 根据审批状态查询
     */
    List<PurchaseRequest> findByApproveStatus(String approveStatus);

    /**
     * 根据采购订单ID查询
     */
    List<PurchaseRequest> findByPurchaseOrderId(Long purchaseOrderId);

    /**
     * 根据状态查询
     */
    List<PurchaseRequest> findByStatus(String status);

    /**
     * 多条件查询
     */
    @Query("SELECT r FROM PurchaseRequest r WHERE " +
           "(:requestNo IS NULL OR r.requestNo LIKE %:requestNo%) AND " +
           "(:partId IS NULL OR r.partId = :partId) AND " +
           "(:urgency IS NULL OR r.urgency = :urgency) AND " +
           "(:requester IS NULL OR r.requester = :requester) AND " +
           "(:approveStatus IS NULL OR r.approveStatus = :approveStatus) AND " +
           "(:status IS NULL OR r.status = :status)")
    Page<PurchaseRequest> search(@Param("requestNo") String requestNo,
                                @Param("partId") Long partId,
                                @Param("urgency") String urgency,
                                @Param("requester") String requester,
                                @Param("approveStatus") String approveStatus,
                                @Param("status") String status,
                                Pageable pageable);
}