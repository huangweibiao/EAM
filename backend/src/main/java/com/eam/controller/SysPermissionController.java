package com.eam.controller;

import com.eam.common.Result;
import com.eam.entity.SysPermission;
import com.eam.service.ISysPermissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单权限 Controller
 */
@RestController
@RequestMapping("/api/system/permission")
public class SysPermissionController {

    private final ISysPermissionService permissionService;

    public SysPermissionController(ISysPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * 获取所有菜单树
     */
    @GetMapping("/tree")
    public Result<List<SysPermission>> getMenuTree() {
        return Result.success(permissionService.getMenuTree());
    }

    /**
     * 根据用户ID获取菜单权限
     */
    @GetMapping("/user/{userId}")
    public Result<List<SysPermission>> getMenusByUserId(@PathVariable Long userId) {
        return Result.success(permissionService.getMenusByUserId(userId));
    }

    /**
     * 新增菜单
     */
    @PostMapping
    public Result<String> add(@RequestBody SysPermission permission) {
        permissionService.save(permission);
        return Result.success("菜单添加成功");
    }

    /**
     * 更新菜单
     */
    @PutMapping
    public Result<String> update(@RequestBody SysPermission permission) {
        permissionService.updateById(permission);
        return Result.success("菜单更新成功");
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        permissionService.removeById(id);
        return Result.success("菜单删除成功");
    }
}