package com.eam.controller;

import org.springframework.data.domain.Page;
import com.eam.common.PageResult;
import com.eam.common.Result;
import com.eam.entity.AssetTransfer;
import com.eam.service.IAssetTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.eam.security.RequirePermission;
import com.eam.annotation.OperationLog;

/**
 * 资产调拨 Controller
 */
@RestController
@RequestMapping("/api/asset/transfer")
public class AssetTransferController {

    @Autowired
    private IAssetTransferService assetTransferService;

    /**
     * 分页查询调拨单
     */
    @GetMapping("/page")
    public Result<PageResult<AssetTransfer>> page(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String status) {
        Page<AssetTransfer> page = assetTransferService.page(pageNum, pageSize, status);
        PageResult<AssetTransfer> result = PageResult.of(
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getContent()
        );
        return Result.success(result);
    }

    /**
     * 获取所有调拨单
     */
    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(assetTransferService.list());
    }

    /**
     * 获取待审批调拨单
     */
    @GetMapping("/pending")
    public Result<?> pending() {
        return Result.success(assetTransferService.listPending());
    }

    /**
     * 根据ID获取调拨单
     */
    @GetMapping("/{id}")
    public Result<AssetTransfer> getById(@PathVariable Long id) {
        return Result.success(assetTransferService.getById(id));
    }

    /**
     * 创建调拨申请
     */
    @PostMapping("/create")
    public Result<AssetTransfer> create(@RequestBody AssetTransfer transfer) {
        return Result.success(assetTransferService.create(transfer));
    }

    /**
     * 审批调拨单
     */
    @PostMapping("/approve")
    @RequirePermission("asset:transfer:approve")
    @OperationLog(value = "审批资产调拨", description = "审批资产调拨申请", operationType = "UPDATE", recordParams = true, recordResult = true)
    public Result<AssetTransfer> approve(@RequestParam Long id,
                                       @RequestParam String approver,
                                       @RequestParam boolean approved,
                                       @RequestParam(required = false) String remark) {
        return Result.success(assetTransferService.approve(id, approver, approved, remark));
    }

    /**
     * 完成调拨单
     */
    @PostMapping("/complete")
    @RequirePermission("asset:transfer:complete")
    @OperationLog(value = "完成资产调拨", description = "完成资产调拨流程", operationType = "UPDATE", recordParams = true, recordResult = true)
    public Result<AssetTransfer> complete(@RequestParam Long id,
                                       @RequestParam(required = false) String operator,
                                       @RequestParam(required = false) String completeRemark) {
        return Result.success(assetTransferService.complete(id, operator, completeRemark));
    }
}