package com.eam.controller;

import com.eam.common.Result;
import com.eam.entity.AssetCategory;
import com.eam.service.IAssetCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 资产分类 Controller
 */
@RestController
@RequestMapping("/api/asset/category")
public class AssetCategoryController {

    @Autowired
    private IAssetCategoryService assetCategoryService;

    /**
     * 获取分类树
     */
    @GetMapping("/tree")
    public Result<?> tree() {
        return Result.success(assetCategoryService.tree());
    }

    /**
     * 获取所有分类列表
     */
    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(assetCategoryService.list());
    }

    /**
     * 根据ID获取分类
     */
    @GetMapping("/{id}")
    public Result<AssetCategory> getById(@PathVariable Long id) {
        return Result.success(assetCategoryService.getById(id));
    }

    /**
     * 新增分类
     */
    @PostMapping("/add")
    public Result<AssetCategory> add(@RequestBody AssetCategory category) {
        return Result.success(assetCategoryService.add(category));
    }

    /**
     * 修改分类
     */
    @PutMapping("/update")
    public Result<AssetCategory> update(@RequestBody AssetCategory category) {
        return Result.success(assetCategoryService.update(category));
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return Result.success(assetCategoryService.delete(id));
    }
}