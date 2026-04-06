package com.eam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eam.entity.SysPermission;

import java.util.List;

/**
 * 菜单权限 Service 接口
 */
public interface ISysPermissionService extends IService<SysPermission> {

    /**
     * 获取所有菜单树形结构
     */
    List<SysPermission> getMenuTree();

    /**
     * 根据用户ID获取菜单权限
     */
    List<SysPermission> getMenusByUserId(Long userId);
}