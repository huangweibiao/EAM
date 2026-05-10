package com.eam.repository;

import com.eam.entity.AssetInventory;
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
 * 资产盘点 Repository
 */
@Repository
public interface AssetInventoryRepository extends JpaRepository<AssetInventory, Long>, JpaSpecificationExecutor<AssetInventory> {

    /**
     * 根据盘点编号查询
     */
    Optional<AssetInventory> findByInventoryNo(String inventoryNo);

    /**
     * 检查盘点编号是否存在
     */
    boolean existsByInventoryNo(String inventoryNo);

    /**
     * 根据部门查询
     */
    List<AssetInventory> findByDeptId(Long deptId);

    /**
     * 根据状态查询
     */
    List<AssetInventory> findByStatus(String status);

    /**
     * 根据创建人查询
     */
    List<AssetInventory> findByCreateBy(String createBy);

    /**
     * 模糊查询盘点名称
     */
    List<AssetInventory> findByInventoryNameContaining(String keyword);

    /**
     * 多条件查询
     */
    @Query("SELECT i FROM AssetInventory i WHERE " +
           "(:inventoryNo IS NULL OR i.inventoryNo LIKE %:inventoryNo%) AND " +
           "(:inventoryName IS NULL OR i.inventoryName LIKE %:inventoryName%) AND " +
           "(:deptId IS NULL OR i.deptId = :deptId) AND " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:createBy IS NULL OR i.createBy = :createBy)")
    Page<AssetInventory> search(@Param("inventoryNo") String inventoryNo,
                               @Param("inventoryName") String inventoryName,
                               @Param("deptId") Long deptId,
                               @Param("status") String status,
                               @Param("createBy") String createBy,
                               Pageable pageable);
}
