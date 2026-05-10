package com.eam.repository;

import com.eam.entity.SysPermission;
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
 * 系统权限 Repository
 */
@Repository
public interface SysPermissionRepository extends JpaRepository<SysPermission, Long>, JpaSpecificationExecutor<SysPermission> {

    /**
     * 根据权限编码查询
     */
    Optional<SysPermission> findByPermCode(String permCode);

    /**
     * 检查权限编码是否存在
     */
    boolean existsByPermCode(String permCode);

    /**
     * 根据父ID查询子权限
     */
    List<SysPermission> findByParentId(Long parentId);

    /**
     * 根据权限类型查询
     */
    List<SysPermission> findByPermType(String permType);

    /**
     * 根据状态查询
     */
    List<SysPermission> findByStatus(Integer status);

    /**
     * 模糊查询权限名称
     */
    List<SysPermission> findByPermNameContaining(String keyword);

    /**
     * 多条件查询
     */
    @Query("SELECT p FROM SysPermission p WHERE " +
           "(:permCode IS NULL OR p.permCode LIKE %:permCode%) AND " +
           "(:permName IS NULL OR p.permName LIKE %:permName%) AND " +
           "(:parentId IS NULL OR p.parentId = :parentId) AND " +
           "(:permType IS NULL OR p.permType = :permType) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<SysPermission> search(@Param("permCode") String permCode,
                               @Param("permName") String permName,
                               @Param("parentId") Long parentId,
                               @Param("permType") String permType,
                               @Param("status") Integer status,
                               Pageable pageable);
}