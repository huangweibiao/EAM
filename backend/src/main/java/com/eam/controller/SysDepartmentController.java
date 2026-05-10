package com.eam.controller;

import com.eam.security.RequirePermission;
import com.eam.common.Result;
import com.eam.entity.SysDepartment;
import com.eam.service.ISysDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统部门 Controller
 */
@RestController
@RequestMapping("/api/system/dept")
public class SysDepartmentController {

    @Autowired
    private ISysDepartmentService sysDepartmentService;

    /**
     * 获取部门树
     */
    @GetMapping("/tree")
    @RequirePermission("system:dept:list")
    public Result<?> tree() {
        return Result.success(sysDepartmentService.tree());
    }

    /**
     * 获取所有部门列表
     */
    @GetMapping("/list")
    @RequirePermission("system:dept:list")
    public Result<?> list() {
        return Result.success(sysDepartmentService.list());
    }

    /**
     * 根据ID获取部门
     */
    @GetMapping("/{id}")
    @RequirePermission("system:dept:list")
    public Result<SysDepartment> getById(@PathVariable Long id) {
        return Result.success(sysDepartmentService.getById(id));
    }

    /**
     * 获取子部门列表
     */
    @GetMapping("/children/{parentId}")
    @RequirePermission("system:dept:list")
    public Result<?> children(@PathVariable Long parentId) {
        return Result.success(sysDepartmentService.listChildren(parentId));
    }

    /**
     * 新增部门
     */
    @PostMapping("/add")
    @RequirePermission("system:dept:add")
    public Result<SysDepartment> add(@RequestBody SysDepartment dept) {
        return Result.success(sysDepartmentService.add(dept));
    }

    /**
     * 修改部门
     */
    @PutMapping("/update")
    @RequirePermission("system:dept:update")
    public Result<SysDepartment> update(@RequestBody SysDepartment dept) {
        return Result.success(sysDepartmentService.update(dept));
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    @RequirePermission("system:dept:delete")
    public Result<?> delete(@PathVariable Long id) {
        return Result.success(sysDepartmentService.delete(id));
    }
}