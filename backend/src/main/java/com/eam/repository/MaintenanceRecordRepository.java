package com.eam.repository;

import com.eam.entity.MaintenanceRecord;
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
 * 维护记录 Repository
 */
@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long>, JpaSpecificationExecutor<MaintenanceRecord> {

    /**
     * 根据维护编码查询
     */
    Optional<MaintenanceRecord> findByMaintenanceCode(String maintenanceCode);

    /**
     * 检查维护编码是否存在
     */
    boolean existsByMaintenanceCode(String maintenanceCode);

    /**
     * 根据计划ID查询维护记录
     */
    List<MaintenanceRecord> findByPlanId(Long planId);

    /**
     * 根据资产ID查询维护记录
     */
    List<MaintenanceRecord> findByAssetId(Long assetId);

    /**
     * 根据资产ID查询维护记录并按维护时间倒序
     */
    List<MaintenanceRecord> findByAssetIdOrderByMaintenanceDateDesc(Long assetId);

    /**
     * 根据维护类型查询
     */
    List<MaintenanceRecord> findByMaintenanceType(String maintenanceType);

    /**
     * 根据技术员查询
     */
    List<MaintenanceRecord> findByTechnician(String technician);

    /**
     * 根据维护结果查询
     */
    List<MaintenanceRecord> findByResult(String result);

    /**
     * 根据创建人查询
     */
    List<MaintenanceRecord> findByCreateBy(String createBy);

    /**
     * 多条件查询
     */
    @Query("SELECT r FROM MaintenanceRecord r WHERE " +
           "(:maintenanceCode IS NULL OR r.maintenanceCode LIKE %:maintenanceCode%) AND " +
           "(:planId IS NULL OR r.planId = :planId) AND " +
           "(:assetId IS NULL OR r.assetId = :assetId) AND " +
           "(:maintenanceType IS NULL OR r.maintenanceType = :maintenanceType) AND " +
           "(:technician IS NULL OR r.technician = :technician) AND " +
           "(:result IS NULL OR r.result = :result)")
    Page<MaintenanceRecord> search(@Param("maintenanceCode") String maintenanceCode,
                                   @Param("planId") Long planId,
                                   @Param("assetId") Long assetId,
                                   @Param("maintenanceType") String maintenanceType,
                                   @Param("technician") String technician,
                                   @Param("result") String result,
                                   Pageable pageable);

    List<MaintenanceRecord> findByMaintenanceDateBetween(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
}