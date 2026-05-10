package com.eam.controller;

import com.eam.annotation.OperationLog;
import com.eam.security.RequirePermission;
import com.eam.common.PageResult;
import com.eam.common.Result;
import com.eam.entity.Asset;
import com.eam.service.IAssetService;
import jakarta.servlet.http.HttpServletResponse;
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
    @Autowired(required = false)
    private com.eam.service.impl.CachedAssetCategoryServiceImpl cachedAssetCategoryService;

    /**
     * 分页查询资产
     */
    @GetMapping("/page")
    @RequirePermission("asset:list")
    @OperationLog(value = "查询资产列表", description = "分页查询资产信息", recordParams = true)
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
                page.getNumber(),
                page.getSize(),
                page.getContent()
        );
        return Result.success(result);
    }

    /**
     * 获取所有资产
     */
    @GetMapping("/list")
    @RequirePermission("asset:list")
    @OperationLog(value = "查询所有资产", description = "获取所有资产列表", recordParams = false)
    public Result<?> list() {
        return Result.success(assetService.listAll());
    }

    /**
     * 根据ID获取资产
     */
    @GetMapping("/{id}")
    @RequirePermission("asset:list")
    @OperationLog(value = "查询资产详情", description = "根据ID获取资产详细信息", recordParams = false)
    public Result<Asset> getById(@PathVariable Long id) {
        return Result.success(assetService.getById(id));
    }

    /**
     * 新增资产
     */
    @PostMapping("/add")
    @RequirePermission("asset:add")
    @OperationLog(value = "新增资产", description = "新增资产信息", operationType = "CREATE", recordParams = true, recordResult = true)
    public Result<Asset> add(@RequestBody Asset asset) {
        return Result.success(assetService.add(asset));
    }

    /**
     * 修改资产
     */
    @PutMapping("/update")
    @RequirePermission("asset:update")
    @OperationLog(value = "修改资产", description = "修改资产信息", operationType = "UPDATE", recordParams = true, recordResult = true)
    public Result<Asset> update(@RequestBody Asset asset) {
        return Result.success(assetService.update(asset));
    }

    /**
     * 删除资产
     */
    @DeleteMapping("/{id}")
    @RequirePermission("asset:delete")
    @OperationLog(value = "删除资产", description = "删除资产信息", operationType = "DELETE", recordParams = true, recordResult = true)
    public Result<?> delete(@PathVariable Long id) {
        return Result.success(assetService.delete(id));
    }

    /**
     * 获取资产分类列表（使用缓存）
     */
    @GetMapping("/categories")
    @RequirePermission("asset:list")
    public Result<?> getCategories() {
        if (cachedAssetCategoryService != null) {
            return Result.success(cachedAssetCategoryService.listAll());
        }
        return Result.success(assetService.listAllCategories());
    }

    /**
     * 获取资产分类树（使用缓存）
     */
    @GetMapping("/categories/tree")
    @RequirePermission("asset:list")
    public Result<?> getCategoryTree() {
        if (cachedAssetCategoryService != null) {
            return Result.success(cachedAssetCategoryService.tree());
        }
        return Result.success(assetService.getCategoryTree());
    }

    /**
     * 根据资产分类获取资产列表（使用缓存）
     */
    @GetMapping("/by-category/{categoryId}")
    @RequirePermission("asset:list")
    public Result<?> getAssetsByCategory(@PathVariable Long categoryId) {
        if (cachedAssetCategoryService != null) {
            return Result.success(assetService.listByCategory(categoryId));
        }
        return Result.success(assetService.listByCategory(categoryId));
    }

    /**
     * 资产调拨
     */
    @PostMapping("/transfer")
    @RequirePermission("asset:transfer")
    @OperationLog(value = "资产调拨", description = "调拨资产", operationType = "UPDATE", recordParams = true, recordResult = true)
    public Result<?> transfer(@RequestParam Long assetId,
                              @RequestParam Long toDeptId,
                              @RequestParam(required = false) Long toUserId,
                              @RequestParam(required = false) String reason,
                              @RequestParam(required = false, defaultValue = "system") String operator) {
        return Result.success(assetService.transfer(assetId, toDeptId, toUserId, reason, operator));
    }

    /**
     * 资产变动
     */
    @PostMapping("/change")
    @RequirePermission("asset:change")
    public Result<?> change(@RequestParam Long assetId,
                            @RequestParam String changeType,
                            @RequestParam String newValue,
                            @RequestParam(required = false) String reason,
                            @RequestParam(required = false, defaultValue = "system") String operator) {
        return Result.success(assetService.change(assetId, changeType, newValue, reason, operator));
    }

    /**
     * 生成资产二维码
     */
    @PostMapping("/{id}/qrcode")
    @RequirePermission("asset:list")
    @OperationLog(value = "生成资产二维码", description = "生成资产二维码", operationType = "READ", recordParams = true, recordResult = false)
    public Result<String> generateQrCode(@PathVariable Long id) {
        String qrCode = assetService.generateAssetQrCode(id);
        return Result.success(qrCode);
    }

    /**
     * 生成资产二维码（自定义尺寸）
     */
    @PostMapping("/{id}/qrcode/custom")
    @RequirePermission("asset:list")
    @OperationLog(value = "生成资产二维码", description = "生成自定义尺寸资产二维码", operationType = "READ", recordParams = true, recordResult = false)
    public Result<String> generateQrCode(@PathVariable Long id,
                                   @RequestParam(defaultValue = "300") Integer width,
                                   @RequestParam(defaultValue = "300") Integer height) {
        String qrCode = assetService.generateAssetQrCodeImage(id, width, height);
        return Result.success(qrCode);
    }

    /**
     * 下载资产二维码
     */
    @GetMapping("/{id}/qrcode/download")
    @RequirePermission("asset:list")
    @OperationLog(value = "下载资产二维码", description = "下载资产二维码图片", operationType = "READ", recordParams = true, recordResult = false)
    public void downloadQrCode(@PathVariable Long id,
                              HttpServletResponse response) {
        try {
            // 获取资产的二维码
            String qrCode = assetService.generateAssetQrCode(id);
            if (qrCode == null) {
                response.setStatus(404);
                return;
            }

            // 解码Base64获取图片数据
            String[] parts = qrCode.split(",");
            if (parts.length < 2) {
                response.setStatus(400);
                return;
            }

            String base64Data = parts[1];
            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);

            // 设置响应头
            response.setContentType("image/png");
            response.setHeader("Content-Disposition", "attachment; filename=\"asset_qrcode_" + id + ".png\"");
            response.setContentLength(imageBytes.length);

            // 输出图片
            response.getOutputStream().write(imageBytes);
            response.getOutputStream().flush();

        } catch (Exception e) {
            try {
                response.setStatus(500);
                response.getWriter().write("下载二维码失败: " + e.getMessage());
            } catch (Exception ex) {
                // 忽略
            }
        }
    }

    /**
     * 批量生成资产二维码
     */
    @PostMapping("/qrcode/batch")
    @RequirePermission("asset:list")
    @OperationLog(value = "批量生成资产二维码", description = "批量生成资产二维码", operationType = "READ", recordParams = true, recordResult = false)
    public Result<java.util.Map<String, Object>> batchGenerateQrCodes(@RequestBody java.util.List<Long> assetIds) {
        java.util.Map<String, String> results = new java.util.HashMap<>();
        java.util.Map<String, String> errors = new java.util.HashMap<>();

        for (Long assetId : assetIds) {
            try {
                String qrCode = assetService.generateAssetQrCode(assetId);
                if (qrCode != null) {
                    results.put("asset_" + assetId, qrCode);
                } else {
                    errors.put("asset_" + assetId, "二维码生成失败");
                }
            } catch (Exception e) {
                errors.put("asset_" + assetId, e.getMessage());
            }
        }

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("success", results);
        response.put("errors", errors);
        response.put("total", assetIds.size());
        response.put("successCount", results.size());
        response.put("errorCount", errors.size());

        return Result.success(response);
    }
}