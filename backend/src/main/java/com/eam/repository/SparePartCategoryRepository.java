package com.eam.repository;

import com.eam.entity.SparePartCategory;
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
 * 备件分类 Repository
 */
@Repository
public interface SparePartCategoryRepository extends JpaRepository<SparePartCategory, Long>, JpaSpecificationExecutor<SparePartCategory> {

    /**
     * 根据分类编码查询
     */
    Optional<SparePartCategory> findByCategoryCode(String categoryCode);

    /**
     * 检查分类编码是否存在
     */
    boolean existsByCategoryCode(String categoryCode);

    /**
     * 根据父ID查询子分类
     */
    List<SparePartCategory> findByParentId(Long parentId);

    /**
     * 根据状态查询
     */
    List<SparePartCategory> findByStatus(Integer status);

    /**
     * 模糊查询分类名称
     */
    List<SparePartCategory> findByCategoryNameContaining(String keyword);

    /**
     * 多条件查询
     */
    @Query("SELECT c FROM SparePartCategory c WHERE " +
           "(:categoryCode IS NULL OR c.categoryCode LIKE %:categoryCode%) AND " +
           "(:categoryName IS NULL OR c.categoryName LIKE %:categoryName%) AND " +
           "(:parentId IS NULL OR c.parentId = :parentId) AND " +
           "(:status IS NULL OR c.status = :status)")
    Page<SparePartCategory> search(@Param("categoryCode") String categoryCode,
                                  @Param("categoryName") String categoryName,
                                  @Param("parentId") Long parentId,
                                  @Param("status") Integer status,
                                  Pageable pageable);
}
