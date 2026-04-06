package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eam.entity.SysPermission;
import com.eam.entity.SysRolePermission;
import com.eam.entity.SysUserRole;
import com.eam.mapper.SysPermissionMapper;
import com.eam.mapper.SysRolePermissionMapper;
import com.eam.mapper.SysUserRoleMapper;
import com.eam.service.ISysPermissionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单权限 Service 实现类
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission>
        implements ISysPermissionService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;

    public SysPermissionServiceImpl(SysUserRoleMapper sysUserRoleMapper,
                                    SysRolePermissionMapper sysRolePermissionMapper) {
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysRolePermissionMapper = sysRolePermissionMapper;
    }

    @Override
    public List<SysPermission> getMenuTree() {
        // 获取所有菜单
        List<SysPermission> allMenus = this.list(new LambdaQueryWrapper<SysPermission>()
                .orderByAsc(SysPermission::getSortOrder));

        // 构建树形结构
        return buildMenuTree(allMenus, 0L);
    }

    @Override
    public List<SysPermission> getMenusByUserId(Long userId) {
        // 获取用户角色
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId));

        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 获取角色权限
        List<SysRolePermission> rolePerms = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .in(SysRolePermission::getRoleId, roleIds));

        if (rolePerms.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> permIds = rolePerms.stream()
                .map(SysRolePermission::getPermissionId)
                .collect(Collectors.toList());

        // 获取权限菜单
        List<SysPermission> userMenus = this.list(new LambdaQueryWrapper<SysPermission>()
                .in(SysPermission::getId, permIds)
                .eq(SysPermission::getStatus, 1)
                .orderByAsc(SysPermission::getSortOrder));

        return buildMenuTree(userMenus, 0L);
    }

    /**
     * 递归构建菜单树
     */
    private List<SysPermission> buildMenuTree(List<SysPermission> allMenus, Long parentId) {
        return allMenus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .peek(menu -> menu.setChildren(buildMenuTree(allMenus, menu.getId())))
                .collect(Collectors.toList());
    }
}