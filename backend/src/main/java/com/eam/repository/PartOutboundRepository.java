package com.eam.repository;

import com.eam.entity.PartOutbound;
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
 * 备件出库记录 Repository
 */
@Repository
public interface PartOutboundRepository extends JpaRepository<PartOutbound, Long>, JpaSpecificationExecutor<PartOutbound> {

    /**
     * 根据出库编号查询
     */
    Optional<PartOutbound> findByOutboundNo(String outboundNo);

    /**
     * 检查出库编号是否存在
     */
    boolean existsByOutboundNo(String outboundNo);

    /**
     * 根据备件ID查询出库记录
     */
    List<PartOutbound> findByPartId(Long partId);

    /**
     * 根据工单ID查询出库记录
     */
    List<PartOutbound> findByWorkOrderId(Long workOrderId);

    /**
     * 根据部门ID查询出库记录
     */
    List<PartOutbound> findByDepartmentId(Long departmentId);

    /**
     * 根据接收人查询
     */
    List<PartOutbound> findByReceiver(String receiver);

    /**
     * 根据检验人查询
     */
    List<PartOutbound> findByChecker(String checker);

    /**
     * 根据创建人查询
     */
    List<PartOutbound> findByCreateBy(String createBy);

    /**
     * 多条件查询
     */
    @Query("SELECT o FROM PartOutbound o WHERE " +
           "(:outboundNo IS NULL OR o.outboundNo LIKE %:outboundNo%) AND " +
           "(:partId IS NULL OR o.partId = :partId) AND " +
           "(:workOrderId IS NULL OR o.workOrderId = :workOrderId) AND " +
           "(:departmentId IS NULL OR o.departmentId = :departmentId) AND " +
           "(:receiver IS NULL OR o.receiver = :receiver) AND " +
           "(:checker IS NULL OR o.checker = :checker)")
    Page<PartOutbound> search(@Param("outboundNo") String outboundNo,
                              @Param("partId") Long partId,
                              @Param("workOrderId") Long workOrderId,
                              @Param("departmentId") Long departmentId,
                              @Param("receiver") String receiver,
                              @Param("checker") String checker,
                              Pageable pageable);
}