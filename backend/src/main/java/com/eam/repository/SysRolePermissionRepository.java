package com.eam.repository;

import com.eam.entity.SysRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统角色权限关联 Repository
 */
@Repository
public interface SysRolePermissionRepository extends JpaRepository<SysRolePermission, Long>, JpaSpecificationExecutor<SysRolePermission> {

    /**
     * 根据角色ID查询权限列表
     */
    List<SysRolePermission> findByRoleId(Long roleId);

    /**
     * 根据角色ID列表查询权限列表
     */
    List<SysRolePermission> findByRoleIdIn(List<Long> roleIds);

    /**
     * 根据权限ID查询角色列表
     */
    List<SysRolePermission> findByPermissionId(Long permissionId);

    /**
     * 根据角色ID和权限ID查询
     */
    SysRolePermission findByRoleIdAndPermissionId(Long roleId, Long permissionId);

    /**
     * 删除角色权限关联
     */
    void deleteByRoleId(Long roleId);

    /**
     * 检查角色权限关联是否存在
     */
    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);
}