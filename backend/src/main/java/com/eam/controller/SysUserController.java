package com.eam.controller;

import org.springframework.data.domain.Page;
import com.eam.annotation.OperationLog;
import com.eam.security.RequirePermission;
import com.eam.common.PageResult;
import com.eam.common.Result;
import com.eam.entity.SysUser;
import com.eam.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统用户 Controller
 */
@RestController
@RequestMapping("/api/system/user")
public class SysUserController {

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 分页查询用户
     */
    @GetMapping("/page")
    @RequirePermission("system:user:list")
    @OperationLog(value = "查询用户列表", description = "分页查询系统用户", operationType = "SELECT", recordParams = true)
    public Result<PageResult<SysUser>> page(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        Page<SysUser> page = sysUserService.page(pageNum, pageSize, username, status);
        PageResult<SysUser> result = PageResult.of(
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getContent()
        );
        return Result.success(result);
    }

    /**
     * 获取所有用户
     */
    @GetMapping("/list")
    @RequirePermission("system:user:list")
    @OperationLog(value = "查询所有用户", description = "获取所有用户列表")
    public Result<?> list() {
        return Result.success(sysUserService.list());
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    @RequirePermission("system:user:list")
    @OperationLog(value = "查询用户详情", description = "根据ID获取用户详细信息")
    public Result<SysUser> getById(@PathVariable Long id) {
        return Result.success(sysUserService.getById(id));
    }

    /**
     * 新增用户
     */
    @PostMapping("/add")
    @RequirePermission("system:user:add")
    @OperationLog(value = "新增用户", description = "新增系统用户", operationType = "CREATE", recordParams = true, recordResult = true)
    public Result<SysUser> add(@RequestBody SysUser user) {
        return Result.success(sysUserService.add(user));
    }

    /**
     * 修改用户
     */
    @PutMapping("/update")
    @RequirePermission("system:user:update")
    @OperationLog(value = "修改用户", description = "修改系统用户信息", operationType = "UPDATE", recordParams = true, recordResult = true)
    public Result<SysUser> update(@RequestBody SysUser user) {
        return Result.success(sysUserService.update(user));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @RequirePermission("system:user:delete")
    @OperationLog(value = "删除用户", description = "删除系统用户", operationType = "DELETE", recordParams = true, recordResult = true)
    public Result<?> delete(@PathVariable Long id) {
        return Result.success(sysUserService.delete(id));
    }

    /**
     * 重置密码
     */
    @PostMapping("/resetPassword/{id}")
    public Result<?> resetPassword(@PathVariable Long id) {
        return Result.success(sysUserService.resetPassword(id));
    }
}