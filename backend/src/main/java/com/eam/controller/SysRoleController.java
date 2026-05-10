package com.eam.controller;

import org.springframework.data.domain.Page;
import com.eam.security.RequirePermission;
import com.eam.common.PageResult;
import com.eam.common.Result;
import com.eam.entity.SysRole;
import com.eam.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统角色 Controller
 */
@RestController
@RequestMapping("/api/system/role")
public class SysRoleController {

    @Autowired
    private ISysRoleService sysRoleService;

    /**
     * 分页查询角色
     */
    @GetMapping("/page")
    @RequirePermission("system:role:list")
    public Result<PageResult<SysRole>> page(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String roleName) {
        Page<SysRole> page = sysRoleService.page(pageNum, pageSize, roleName);
        PageResult<SysRole> result = PageResult.of(
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getContent()
        );
        return Result.success(result);
    }

    /**
     * 获取所有角色
     */
    @GetMapping("/list")
    @RequirePermission("system:role:list")
    public Result<?> list() {
        return Result.success(sysRoleService.listAll());
    }

    /**
     * 根据ID获取角色
     */
    @GetMapping("/{id}")
    @RequirePermission("system:role:list")
    public Result<SysRole> getById(@PathVariable Long id) {
        return Result.success(sysRoleService.getById(id));
    }

    /**
     * 新增角色
     */
    @PostMapping("/add")
    @RequirePermission("system:role:add")
    public Result<SysRole> add(@RequestBody SysRole role) {
        return Result.success(sysRoleService.add(role));
    }

    /**
     * 修改角色
     */
    @PutMapping("/update")
    @RequirePermission("system:role:update")
    public Result<SysRole> update(@RequestBody SysRole role) {
        return Result.success(sysRoleService.update(role));
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @RequirePermission("system:role:delete")
    public Result<?> delete(@PathVariable Long id) {
        return Result.success(sysRoleService.delete(id));
    }

    /**
     * 分配权限
     */
    @PutMapping("/perm")
    @RequirePermission("system:role:perm")
    @OperationLog(value = "分配权限", description = "为角色分配权限", operationType = "UPDATE", recordParams = true, recordResult = true)
    public Result<?> assignPermissions(@RequestParam Long roleId, @RequestBody List<Long> permIds) {
        return Result.success(sysRoleService.assignPermissions(roleId, permIds));
    }
}