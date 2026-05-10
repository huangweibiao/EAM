package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.SysPermission;
import com.eam.entity.SysRolePermission;
import com.eam.entity.SysUserRole;
import com.eam.repository.SysPermissionRepository;
import com.eam.repository.SysRolePermissionRepository;
import com.eam.repository.SysUserRoleRepository;
import com.eam.service.ISysPermissionService;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单权限 Service 实现类
 */
@Service
public class SysPermissionServiceImpl implements ISysPermissionService {

    private final SysUserRoleRepository sysUserRoleRepository;
    private final SysRolePermissionRepository sysRolePermissionRepository;
    private final SysPermissionRepository permissionRepository;

    public SysPermissionServiceImpl(SysUserRoleRepository sysUserRoleRepository,
                                     SysRolePermissionRepository sysRolePermissionRepository,
                                     SysPermissionRepository permissionRepository) {
        this.sysUserRoleRepository = sysUserRoleRepository;
        this.sysRolePermissionRepository = sysRolePermissionRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<SysPermission> getMenuTree() {
        // 获取所有菜单
        List<SysPermission> allMenus = permissionRepository.findAll(
                Sort.by(Sort.Direction.ASC, "sortOrder"));

        // 构建树形结构
        return buildMenuTree(allMenus, 0L);
    }

    @Override
    public List<SysPermission> getMenusByUserId(Long userId) {
        // 获取用户角色
        List<SysUserRole> userRoles = sysUserRoleRepository.findByUserId(userId);

        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 获取角色权限
        List<SysRolePermission> rolePerms = sysRolePermissionRepository.findByRoleIdIn(roleIds);

        if (rolePerms.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> permIds = rolePerms.stream()
                .map(SysRolePermission::getPermissionId)
                .collect(Collectors.toList());

        // 获取权限菜单
        Specification<SysPermission> spec = (root, query, cb) -> cb.and(
                root.get("id").in(permIds),
                cb.equal(root.get("status"), 1)
        );
        List<SysPermission> userMenus = permissionRepository.findAll(spec,
                Sort.by(Sort.Direction.ASC, "sortOrder"));

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

    @Override
    public SysPermission getById(Long id) {
        return permissionRepository.findById(id).orElse(null);
    }

    @Override
    public List<SysPermission> list() {
        return permissionRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder"));
    }

    @Override
    public SysPermission save(SysPermission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public SysPermission updateById(SysPermission permission) {
        if (permission.getId() == null) {
            throw new BusinessException("权限ID不能为空");
        }
        return permissionRepository.save(permission);
    }

    @Override
    public boolean removeById(Long id) {
        permissionRepository.deleteById(id);
        return true;
    }
}