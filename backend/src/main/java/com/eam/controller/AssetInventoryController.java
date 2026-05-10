package com.eam.controller;

import org.springframework.data.domain.Page;
import com.eam.common.PageResult;
import com.eam.common.Result;
import com.eam.entity.AssetInventory;
import com.eam.entity.AssetInventoryDetail;
import com.eam.service.IAssetInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 资产盘点 Controller
 */
@RestController
@RequestMapping("/api/asset/inventory")
public class AssetInventoryController {

    @Autowired
    private IAssetInventoryService inventoryService;

    /**
     * 分页查询盘点单
     */
    @GetMapping("/page")
    public Result<PageResult<AssetInventory>> page(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String status) {
        Page<AssetInventory> page = inventoryService.page(pageNum, pageSize, status);
        PageResult<AssetInventory> result = PageResult.of(
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getContent()
        );
        return Result.success(result);
    }

    /**
     * 获取所有盘点单
     */
    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(inventoryService.list());
    }

    /**
     * 根据ID获取盘点单
     */
    @GetMapping("/{id}")
    public Result<AssetInventory> getById(@PathVariable Long id) {
        return Result.success(inventoryService.getById(id));
    }

    /**
     * 获取盘点明细
     */
    @GetMapping("/{id}/details")
    public Result<?> details(@PathVariable Long id) {
        return Result.success(inventoryService.getDetails(id));
    }

    /**
     * 创建盘点单
     */
    @PostMapping("/create")
    public Result<AssetInventory> create(@RequestBody AssetInventory inventory) {
        return Result.success(inventoryService.create(inventory));
    }

    /**
     * 完成盘点
     */
    @PostMapping("/complete")
    public Result<AssetInventory> complete(@RequestParam Long id) {
        return Result.success(inventoryService.complete(id));
    }

    /**
     * 添加盘点明细
     */
    @PostMapping("/detail")
    public Result<?> addDetail(@RequestBody AssetInventoryDetail detail) {
        return Result.success(inventoryService.addDetail(detail));
    }

    /**
     * 更新盘点明细
     */
    @PutMapping("/detail")
    public Result<?> updateDetail(@RequestBody AssetInventoryDetail detail) {
        return Result.success(inventoryService.updateDetail(detail));
    }
}