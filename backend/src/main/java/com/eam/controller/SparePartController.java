package com.eam.controller;

import org.springframework.data.domain.Page;
import com.eam.annotation.OperationLog;
import com.eam.security.RequirePermission;
import com.eam.common.PageResult;
import com.eam.common.Result;
import com.eam.entity.PartInbound;
import com.eam.entity.PartOutbound;
import com.eam.entity.SparePart;
import com.eam.service.IPartInboundService;
import com.eam.service.IPartOutboundService;
import com.eam.service.ISparePartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 备件管理 Controller
 */
@RestController
@RequestMapping("/api/part")
public class SparePartController {

    @Autowired
    private ISparePartService sparePartService;

    @Autowired
    private IPartInboundService inboundService;

    @Autowired
    private IPartOutboundService outboundService;

    // ========== 备件管理 ==========

    /**
     * 分页查询备件
     */
    @GetMapping("/page")
    @RequirePermission("part:list")
    @OperationLog(value = "查询备件列表", description = "分页查询备件信息")
    public Result<PageResult<SparePart>> page(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status) {
        Page<SparePart> page = sparePartService.page(pageNum, pageSize, keyword, categoryId, status);
        PageResult<SparePart> result = PageResult.of(
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getContent()
        );
        return Result.success(result);
    }

    /**
     * 获取所有备件
     */
    @GetMapping("/list")
    @RequirePermission("part:list")
    @OperationLog(value = "查询所有备件", description = "获取所有备件列表")
    public Result<?> list() {
        return Result.success(sparePartService.listAll());
    }

    /**
     * 获取库存预警备件
     */
    @GetMapping("/warn")
    @RequirePermission("part:list")
    @OperationLog(value = "查询库存预警备件", description = "获取库存预警备件列表")
    public Result<?> warning() {
        return Result.success(sparePartService.listWarning());
    }

    /**
     * 根据ID获取备件
     */
    @GetMapping("/{id}")
    @RequirePermission("part:list")
    @OperationLog(value = "查询备件详情", description = "根据ID获取备件详细信息")
    public Result<SparePart> getById(@PathVariable Long id) {
        return Result.success(sparePartService.getById(id));
    }

    /**
     * 新增备件
     */
    @PostMapping("/add")
    @RequirePermission("part:add")
    @OperationLog(value = "新增备件", description = "新增备件信息", operationType = "CREATE", recordParams = true, recordResult = true)
    public Result<SparePart> add(@RequestBody SparePart sparePart) {
        return Result.success(sparePartService.add(sparePart));
    }

    /**
     * 修改备件
     */
    @PutMapping("/update")
    @RequirePermission("part:update")
    @OperationLog(value = "修改备件", description = "修改备件信息", operationType = "UPDATE", recordParams = true, recordResult = true)
    public Result<SparePart> update(@RequestBody SparePart sparePart) {
        return Result.success(sparePartService.update(sparePart));
    }

    /**
     * 删除备件
     */
    @DeleteMapping("/{id}")
    @RequirePermission("part:delete")
    @OperationLog(value = "删除备件", description = "删除备件信息", operationType = "DELETE", recordParams = true, recordResult = true)
    public Result<?> delete(@PathVariable Long id) {
        return Result.success(sparePartService.delete(id));
    }

    // ========== 入库管理 ==========

    /**
     * 分页查询入库记录
     */
    @GetMapping("/inbound/page")
    @RequirePermission("part:inbound:list")
    @OperationLog(value = "查询入库记录", description = "分页查询入库记录")
    public Result<PageResult<PartInbound>> inboundPage(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long partId) {
        Page<PartInbound> page = inboundService.page(pageNum, pageSize, partId);
        PageResult<PartInbound> result = PageResult.of(
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getContent()
        );
        return Result.success(result);
    }

    /**
     * 新增入库
     */
    @PostMapping("/inbound")
    @RequirePermission("part:inbound:add")
    @OperationLog(value = "新增入库", description = "新增备件入库记录", operationType = "CREATE", recordParams = true, recordResult = true)
    public Result<PartInbound> inbound(@RequestBody PartInbound inbound) {
        return Result.success(inboundService.add(inbound));
    }

    // ========== 出库管理 ==========

    /**
     * 分页查询出库记录
     */
    @GetMapping("/outbound/page")
    @RequirePermission("part:outbound:list")
    @OperationLog(value = "查询出库记录", description = "分页查询出库记录")
    public Result<PageResult<PartOutbound>> outboundPage(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long partId) {
        Page<PartOutbound> page = outboundService.page(pageNum, pageSize, partId);
        PageResult<PartOutbound> result = PageResult.of(
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getContent()
        );
        return Result.success(result);
    }

    /**
     * 新增出库
     */
    @PostMapping("/outbound")
    @RequirePermission("part:outbound:add")
    @OperationLog(value = "新增出库", description = "新增备件出库记录", operationType = "CREATE", recordParams = true, recordResult = true)
    public Result<PartOutbound> outbound(@RequestBody PartOutbound outbound) {
        return Result.success(outboundService.add(outbound));
    }
}