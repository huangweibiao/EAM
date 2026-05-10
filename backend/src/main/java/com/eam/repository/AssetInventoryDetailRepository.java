package com.eam.repository;

import com.eam.entity.AssetInventoryDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 资产盘点明细 Repository
 */
@Repository
public interface AssetInventoryDetailRepository extends JpaRepository<AssetInventoryDetail, Long>, JpaSpecificationExecutor<AssetInventoryDetail> {

    /**
     * 根据盘点ID查询明细
     */
    List<AssetInventoryDetail> findByInventoryId(Long inventoryId);

    /**
     * 根据资产ID查询盘点记录
     */
    List<AssetInventoryDetail> findByAssetId(Long assetId);

    /**
     * 根据盘点人查询
     */
    List<AssetInventoryDetail> findByInventoryBy(String inventoryBy);

    /**
     * 根据匹配状态查询
     */
    List<AssetInventoryDetail> findByIsMatch(Integer isMatch);

    /**
     * 根据盘点ID和匹配状态查询
     */
    List<AssetInventoryDetail> findByInventoryIdAndIsMatch(Long inventoryId, Integer isMatch);

    /**
     * 多条件查询
     */
    @Query("SELECT d FROM AssetInventoryDetail d WHERE " +
           "(:inventoryId IS NULL OR d.inventoryId = :inventoryId) AND " +
           "(:assetId IS NULL OR d.assetId = :assetId) AND " +
           "(:isMatch IS NULL OR d.isMatch = :isMatch) AND " +
           "(:inventoryBy IS NULL OR d.inventoryBy = :inventoryBy)")
    Page<AssetInventoryDetail> search(@Param("inventoryId") Long inventoryId,
                                      @Param("assetId") Long assetId,
                                      @Param("isMatch") Integer isMatch,
                                      @Param("inventoryBy") String inventoryBy,
                                      Pageable pageable);
}