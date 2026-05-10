package com.eam.repository;

import com.eam.entity.SysUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统用户角色关联 Repository
 */
@Repository
public interface SysUserRoleRepository extends JpaRepository<SysUserRole, Long>, JpaSpecificationExecutor<SysUserRole> {

    /**
     * 根据用户ID查询角色列表
     */
    List<SysUserRole> findByUserId(Long userId);

    /**
     * 根据角色ID查询用户列表
     */
    List<SysUserRole> findByRoleId(Long roleId);

    /**
     * 根据用户ID和角色ID查询
     */
    SysUserRole findByUserIdAndRoleId(Long userId, Long roleId);

    /**
     * 删除用户角色关联
     */
    void deleteByUserId(Long userId);

    /**
     * 检查用户角色关联是否存在
     */
    boolean existsByUserIdAndRoleId(Long userId, Long roleId);
}