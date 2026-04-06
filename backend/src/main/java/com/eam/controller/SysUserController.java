package com.eam.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
    public Result<PageResult<SysUser>> page(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        IPage<SysUser> page = sysUserService.page(pageNum, pageSize, username, status);
        PageResult<SysUser> result = PageResult.of(
                page.getTotal(),
                page.getCurrent(),
                page.getSize(),
                page.getRecords()
        );
        return Result.success(result);
    }

    /**
     * 获取所有用户
     */
    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(sysUserService.list());
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        return Result.success(sysUserService.getById(id));
    }

    /**
     * 新增用户
     */
    @PostMapping("/add")
    public Result<SysUser> add(@RequestBody SysUser user) {
        return Result.success(sysUserService.add(user));
    }

    /**
     * 修改用户
     */
    @PutMapping("/update")
    public Result<SysUser> update(@RequestBody SysUser user) {
        return Result.success(sysUserService.update(user));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
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