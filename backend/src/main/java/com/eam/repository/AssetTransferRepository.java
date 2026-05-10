package com.eam.repository;

import com.eam.entity.AssetTransfer;
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
 * 资产调拨 Repository
 */
@Repository
public interface AssetTransferRepository extends JpaRepository<AssetTransfer, Long>, JpaSpecificationExecutor<AssetTransfer> {

    /**
     * 根据调拨编号查询
     */
    Optional<AssetTransfer> findByTransferNo(String transferNo);

    /**
     * 检查调拨编号是否存在
     */
    boolean existsByTransferNo(String transferNo);

    /**
     * 根据资产ID查询调拨记录
     */
    List<AssetTransfer> findByAssetId(Long assetId);

    /**
     * 根据调出部门查询
     */
    List<AssetTransfer> findByFromDeptId(Long fromDeptId);

    /**
     * 根据调入部门查询
     */
    List<AssetTransfer> findByToDeptId(Long toDeptId);

    /**
     * 根据调出用户查询
     */
    List<AssetTransfer> findByFromUserId(Long fromUserId);

    /**
     * 根据调入用户查询
     */
    List<AssetTransfer> findByToUserId(Long toUserId);

    /**
     * 根据状态查询
     */
    List<AssetTransfer> findByStatus(String status);

    /**
     * 根据状态查询并按创建时间倒序
     */
    List<AssetTransfer> findByStatusOrderByCreateTimeDesc(String status);

    /**
     * 根据操作人查询
     */
    List<AssetTransfer> findByOperator(String operator);

    /**
     * 多条件查询
     */
    @Query("SELECT t FROM AssetTransfer t WHERE " +
           "(:transferNo IS NULL OR t.transferNo LIKE %:transferNo%) AND " +
           "(:assetId IS NULL OR t.assetId = :assetId) AND " +
           "(:fromDeptId IS NULL OR t.fromDeptId = :fromDeptId) AND " +
           "(:toDeptId IS NULL OR t.toDeptId = :toDeptId) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:operator IS NULL OR t.operator = :operator)")
    Page<AssetTransfer> search(@Param("transferNo") String transferNo,
                                @Param("assetId") Long assetId,
                                @Param("fromDeptId") Long fromDeptId,
                                @Param("toDeptId") Long toDeptId,
                                @Param("status") String status,
                                @Param("operator") String operator,
                                Pageable pageable);
}