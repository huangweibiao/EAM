package com.eam.service;

import com.eam.entity.SysPermission;

import java.util.List;

/**
 * 菜单权限 Service 接口
 */
public interface ISysPermissionService {

    /**
     * 获取所有菜单树形结构
     */
    List<SysPermission> getMenuTree();

    /**
     * 根据用户ID获取菜单权限
     */
    List<SysPermission> getMenusByUserId(Long userId);

    SysPermission getById(Long id);

    List<SysPermission> list();

    SysPermission save(SysPermission permission);

    SysPermission updateById(SysPermission permission);

    boolean removeById(Long id);
}