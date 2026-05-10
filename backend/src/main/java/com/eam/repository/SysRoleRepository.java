package com.eam.repository;

import com.eam.entity.SysRole;
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
 * 系统角色 Repository
 */
@Repository
public interface SysRoleRepository extends JpaRepository<SysRole, Long>, JpaSpecificationExecutor<SysRole> {

    /**
     * 根据角色标识查询
     */
    Optional<SysRole> findByRoleKey(String roleKey);

    /**
     * 检查角色标识是否存在
     */
    boolean existsByRoleKey(String roleKey);

    /**
     * 根据角色名称查询
     */
    Optional<SysRole> findByRoleName(String roleName);

    /**
     * 检查角色名称是否存在
     */
    boolean existsByRoleName(String roleName);

    /**
     * 根据状态查询
     */
    List<SysRole> findByStatus(Integer status);

    /**
     * 模糊查询角色名称
     */
    List<SysRole> findByRoleNameContaining(String keyword);

    /**
     * 多条件查询
     */
    @Query("SELECT r FROM SysRole r WHERE " +
           "(:roleKey IS NULL OR r.roleKey LIKE %:roleKey%) AND " +
           "(:roleName IS NULL OR r.roleName LIKE %:roleName%) AND " +
           "(:status IS NULL OR r.status = :status)")
    Page<SysRole> search(@Param("roleKey") String roleKey,
                        @Param("roleName") String roleName,
                        @Param("status") Integer status,
                        Pageable pageable);
}