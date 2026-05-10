package com.eam.repository;

import com.eam.entity.Asset;
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
 * 资产 Repository
 */
@Repository
public interface AssetRepository extends JpaRepository<Asset, Long>, JpaSpecificationExecutor<Asset> {

    /**
     * 根据资产编码查询
     */
    Optional<Asset> findByAssetCode(String assetCode);

    /**
     * 检查资产编码是否存在
     */
    boolean existsByAssetCode(String assetCode);

    /**
     * 根据分类查询
     */
    List<Asset> findByCategoryId(Long categoryId);

    /**
     * 根据部门查询
     */
    List<Asset> findByDeptId(Long deptId);

    /**
     * 根据状态查询
     */
    List<Asset> findByStatus(String status);

    /**
     * 根据分类和状态查询
     */
    List<Asset> findByCategoryIdAndStatus(Long categoryId, String status);

    /**
     * 模糊查询资产名称
     */
    List<Asset> findByAssetNameContaining(String keyword);

    /**
     * 根据用户查询
     */
    List<Asset> findByUserId(Long userId);

    /**
     * 多条件查询
     */
    @Query("SELECT a FROM Asset a WHERE " +
           "(:keyword IS NULL OR a.assetCode LIKE %:keyword% OR a.assetName LIKE %:keyword%) AND " +
           "(:categoryId IS NULL OR a.categoryId = :categoryId) AND " +
           "(:deptId IS NULL OR a.deptId = :deptId) AND " +
           "(:status IS NULL OR a.status = :status)")
    Page<Asset> search(@Param("keyword") String keyword,
                       @Param("categoryId") Long categoryId,
                       @Param("deptId") Long deptId,
                       @Param("status") String status,
                       Pageable pageable);
}