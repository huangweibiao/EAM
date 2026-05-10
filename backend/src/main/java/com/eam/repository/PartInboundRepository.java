package com.eam.repository;

import com.eam.entity.PartInbound;
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
 * 备件入库记录 Repository
 */
@Repository
public interface PartInboundRepository extends JpaRepository<PartInbound, Long>, JpaSpecificationExecutor<PartInbound> {

    /**
     * 根据入库编号查询
     */
    Optional<PartInbound> findByInboundNo(String inboundNo);

    /**
     * 检查入库编号是否存在
     */
    boolean existsByInboundNo(String inboundNo);

    /**
     * 根据备件ID查询入库记录
     */
    List<PartInbound> findByPartId(Long partId);

    /**
     * 根据采购订单ID查询入库记录
     */
    List<PartInbound> findByPurchaseOrderId(Long purchaseOrderId);

    /**
     * 根据供应商ID查询入库记录
     */
    List<PartInbound> findBySupplierId(Long supplierId);

    /**
     * 根据批号查询入库记录
     */
    List<PartInbound> findByBatchNo(String batchNo);

    /**
     * 根据检验人查询
     */
    List<PartInbound> findByChecker(String checker);

    /**
     * 根据创建人查询
     */
    List<PartInbound> findByCreateBy(String createBy);

    /**
     * 多条件查询
     */
    @Query("SELECT i FROM PartInbound i WHERE " +
           "(:inboundNo IS NULL OR i.inboundNo LIKE %:inboundNo%) AND " +
           "(:partId IS NULL OR i.partId = :partId) AND " +
           "(:purchaseOrderId IS NULL OR i.purchaseOrderId = :purchaseOrderId) AND " +
           "(:supplierId IS NULL OR i.supplierId = :supplierId) AND " +
           "(:batchNo IS NULL OR i.batchNo = :batchNo) AND " +
           "(:checker IS NULL OR i.checker = :checker)")
    Page<PartInbound> search(@Param("inboundNo") String inboundNo,
                            @Param("partId") Long partId,
                            @Param("purchaseOrderId") Long purchaseOrderId,
                            @Param("supplierId") Long supplierId,
                            @Param("batchNo") String batchNo,
                            @Param("checker") String checker,
                            Pageable pageable);
}