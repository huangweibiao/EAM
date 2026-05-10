package com.eam.repository;

import com.eam.entity.MaintenancePlan;
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
 * 维护计划 Repository
 */
@Repository
public interface MaintenancePlanRepository extends JpaRepository<MaintenancePlan, Long>, JpaSpecificationExecutor<MaintenancePlan> {

    /**
     * 根据计划编码查询
     */
    Optional<MaintenancePlan> findByPlanCode(String planCode);

    /**
     * 检查计划编码是否存在
     */
    boolean existsByPlanCode(String planCode);

    /**
     * 根据资产ID查询维护计划
     */
    List<MaintenancePlan> findByAssetId(Long assetId);

    /**
     * 根据维护类型查询
     */
    List<MaintenancePlan> findByMaintenanceType(String maintenanceType);

    /**
     * 根据周期类型查询
     */
    List<MaintenancePlan> findByCycleType(String cycleType);

    /**
     * 根据状态查询
     */
    List<MaintenancePlan> findByStatus(String status);

    /**
     * 根据状态查询并按下次执行时间升序
     */
    List<MaintenancePlan> findByStatusOrderByNextExecuteTimeAsc(String status);

    /**
     * 查询即将到期的维护计划
     */
    @Query("SELECT p FROM MaintenancePlan p WHERE p.status = :status AND p.nextExecuteTime <= :expiringDate")
    List<MaintenancePlan> findExpiringPlans(@Param("status") String status, @Param("expiringDate") java.time.LocalDateTime expiringDate);

    /**
     * 根据负责人查询
     */
    List<MaintenancePlan> findByResponsiblePerson(String responsiblePerson);

    /**
     * 根据创建人查询
     */
    List<MaintenancePlan> findByCreateBy(String createBy);

    /**
     * 多条件查询
     */
    @Query("SELECT p FROM MaintenancePlan p WHERE " +
           "(:planCode IS NULL OR p.planCode LIKE %:planCode%) AND " +
           "(:assetId IS NULL OR p.assetId = :assetId) AND " +
           "(:maintenanceType IS NULL OR p.maintenanceType = :maintenanceType) AND " +
           "(:cycleType IS NULL OR p.cycleType = :cycleType) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:responsiblePerson IS NULL OR p.responsiblePerson = :responsiblePerson)")
    Page<MaintenancePlan> search(@Param("planCode") String planCode,
                                 @Param("assetId") Long assetId,
                                 @Param("maintenanceType") String maintenanceType,
                                 @Param("cycleType") String cycleType,
                                 @Param("status") String status,
                                 @Param("responsiblePerson") String responsiblePerson,
                                 Pageable pageable);
}