package com.eam.controller;

import com.eam.common.PageResult;
import com.eam.common.Result;
import com.eam.entity.Asset;
import com.eam.service.IAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 资产管理 Controller
 */
@RestController
@RequestMapping("/api/asset")
public class AssetController {

    @Autowired
    private IAssetService assetService;

    /**
     * 分页查询资产
     */
    @GetMapping("/page")
    public Result<PageResult<Asset>> page(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) String status) {
        Page<Asset> page = assetService.page(pageNum, pageSize, keyword, categoryId, deptId, status);
        PageResult<Asset> result = PageResult.of(
                page.getTotalElements(),
                (long) page.getNumber(),
                (long) page.getSize(),
                page.getContent()
        );
        return Result.success(result);
    }

    /**
     * 获取所有资产
     */
    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(assetService.listAll());
    }

    /**
     * 根据ID获取资产
     */
    @GetMapping("/{id}")
    public Result<Asset> getById(@PathVariable Long id) {
        return Result.success(assetService.getById(id));
    }

    /**
     * 新增资产
     */
    @PostMapping("/add")
    public Result<Asset> add(@RequestBody Asset asset) {
        return Result.success(assetService.add(asset));
    }

    /**
     * 修改资产
     */
    @PutMapping("/update")
    public Result<Asset> update(@RequestBody Asset asset) {
        return Result.success(assetService.update(asset));
    }

    /**
     * 删除资产
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return Result.success(assetService.delete(id));
    }

    /**
     * 资产变动
     */
    @PostMapping("/change")
    public Result<?> change(@RequestParam Long assetId,
                            @RequestParam String changeType,
                            @RequestParam String newValue,
                            @RequestParam(required = false) String reason,
                            @RequestParam(required = false, defaultValue = "system") String operator) {
        return Result.success(assetService.change(assetId, changeType, newValue, reason, operator));
    }

    /**
     * 资产调拨
     */
    @PostMapping("/transfer")
    public Result<?> transfer(@RequestParam Long assetId,
                              @RequestParam Long toDeptId,
                              @RequestParam(required = false) Long toUserId,
                              @RequestParam(required = false) String reason,
                              @RequestParam(required = false, defaultValue = "system") String operator) {
        return Result.success(assetService.transfer(assetId, toDeptId, toUserId, reason, operator));
    }
}