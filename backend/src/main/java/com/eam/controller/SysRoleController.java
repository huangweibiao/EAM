package com.eam.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
    public Result<PageResult<SysRole>> page(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String roleName) {
        IPage<SysRole> page = sysRoleService.page(pageNum, pageSize, roleName);
        PageResult<SysRole> result = PageResult.of(
                page.getTotal(),
                page.getCurrent(),
                page.getSize(),
                page.getRecords()
        );
        return Result.success(result);
    }

    /**
     * 获取所有角色
     */
    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(sysRoleService.listAll());
    }

    /**
     * 根据ID获取角色
     */
    @GetMapping("/{id}")
    public Result<SysRole> getById(@PathVariable Long id) {
        return Result.success(sysRoleService.getById(id));
    }

    /**
     * 新增角色
     */
    @PostMapping("/add")
    public Result<SysRole> add(@RequestBody SysRole role) {
        return Result.success(sysRoleService.add(role));
    }

    /**
     * 修改角色
     */
    @PutMapping("/update")
    public Result<SysRole> update(@RequestBody SysRole role) {
        return Result.success(sysRoleService.update(role));
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return Result.success(sysRoleService.delete(id));
    }

    /**
     * 分配权限
     */
    @PutMapping("/perm")
    public Result<?> assignPermissions(@RequestParam Long roleId, @RequestBody List<Long> permIds) {
        return Result.success(sysRoleService.assignPermissions(roleId, permIds));
    }
}