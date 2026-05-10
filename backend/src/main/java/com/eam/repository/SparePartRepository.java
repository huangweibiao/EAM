package com.eam.repository;

import com.eam.entity.SparePart;
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
 * 备件 Repository
 */
@Repository
public interface SparePartRepository extends JpaRepository<SparePart, Long>, JpaSpecificationExecutor<SparePart> {

    /**
     * 根据备件编码查询
     */
    Optional<SparePart> findByPartCode(String partCode);

    /**
     * 检查备件编码是否存在
     */
    boolean existsByPartCode(String partCode);

    /**
     * 根据分类ID查询备件
     */
    List<SparePart> findByCategoryId(Long categoryId);

    /**
     * 根据供应商ID查询备件
     */
    List<SparePart> findBySupplierId(Long supplierId);

    /**
     * 根据状态查询
     */
    List<SparePart> findByStatus(String status);

    /**
     * 模糊查询备件名称
     */
    List<SparePart> findByPartNameContaining(String keyword);

    /**
     * 根据型号查询
     */
    List<SparePart> findByModel(String model);

    /**
     * 查询库存低于最小库存的备件
     */
    @Query("SELECT s FROM SparePart s WHERE s.quantity < s.minQuantity AND s.status = '1'")
    List<SparePart> findLowStockParts();

    /**
     * 多条件查询
     */
    @Query("SELECT s FROM SparePart s WHERE " +
           "(:partCode IS NULL OR s.partCode LIKE %:partCode%) AND " +
           "(:partName IS NULL OR s.partName LIKE %:partName%) AND " +
           "(:categoryId IS NULL OR s.categoryId = :categoryId) AND " +
           "(:supplierId IS NULL OR s.supplierId = :supplierId) AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:model IS NULL OR s.model = :model)")
    Page<SparePart> search(@Param("partCode") String partCode,
                          @Param("partName") String partName,
                          @Param("categoryId") Long categoryId,
                          @Param("supplierId") Long supplierId,
                          @Param("status") String status,
                          @Param("model") String model,
                          Pageable pageable);
}