package com.eam.repository;

import com.eam.entity.AssetChangeLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 资产变动记录 Repository
 */
@Repository
public interface AssetChangeLogRepository extends JpaRepository<AssetChangeLog, Long>, JpaSpecificationExecutor<AssetChangeLog> {

    /**
     * 根据资产ID查询变动记录
     */
    List<AssetChangeLog> findByAssetId(Long assetId);

    /**
     * 根据变动类型查询
     */
    List<AssetChangeLog> findByChangeType(String changeType);

    /**
     * 根据操作人查询
     */
    List<AssetChangeLog> findByOperator(String operator);

    /**
     * 根据资产ID和变动类型查询
     */
    List<AssetChangeLog> findByAssetIdAndChangeType(Long assetId, String changeType);

    /**
     * 多条件查询
     */
    @Query("SELECT l FROM AssetChangeLog l WHERE " +
           "(:assetId IS NULL OR l.assetId = :assetId) AND " +
           "(:changeType IS NULL OR l.changeType = :changeType) AND " +
           "(:operator IS NULL OR l.operator = :operator)")
    Page<AssetChangeLog> search(@Param("assetId") Long assetId,
                               @Param("changeType") String changeType,
                               @Param("operator") String operator,
                               Pageable pageable);
}
