package com.eam.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
    public Result<PageResult<SparePart>> page(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status) {
        IPage<SparePart> page = sparePartService.page(pageNum, pageSize, keyword, categoryId, status);
        PageResult<SparePart> result = PageResult.of(
                page.getTotal(),
                page.getCurrent(),
                page.getSize(),
                page.getRecords()
        );
        return Result.success(result);
    }

    /**
     * 获取所有备件
     */
    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(sparePartService.listAll());
    }

    /**
     * 获取库存预警备件
     */
    @GetMapping("/warn")
    public Result<?> warning() {
        return Result.success(sparePartService.listWarning());
    }

    /**
     * 根据ID获取备件
     */
    @GetMapping("/{id}")
    public Result<SparePart> getById(@PathVariable Long id) {
        return Result.success(sparePartService.getById(id));
    }

    /**
     * 新增备件
     */
    @PostMapping("/add")
    public Result<SparePart> add(@RequestBody SparePart sparePart) {
        return Result.success(sparePartService.add(sparePart));
    }

    /**
     * 修改备件
     */
    @PutMapping("/update")
    public Result<SparePart> update(@RequestBody SparePart sparePart) {
        return Result.success(sparePartService.update(sparePart));
    }

    /**
     * 删除备件
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return Result.success(sparePartService.delete(id));
    }

    // ========== 入库管理 ==========

    /**
     * 分页查询入库记录
     */
    @GetMapping("/inbound/page")
    public Result<PageResult<PartInbound>> inboundPage(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long partId) {
        IPage<PartInbound> page = inboundService.page(pageNum, pageSize, partId);
        PageResult<PartInbound> result = PageResult.of(
                page.getTotal(),
                page.getCurrent(),
                page.getSize(),
                page.getRecords()
        );
        return Result.success(result);
    }

    /**
     * 新增入库
     */
    @PostMapping("/inbound")
    public Result<PartInbound> inbound(@RequestBody PartInbound inbound) {
        return Result.success(inboundService.add(inbound));
    }

    // ========== 出库管理 ==========

    /**
     * 分页查询出库记录
     */
    @GetMapping("/outbound/page")
    public Result<PageResult<PartOutbound>> outboundPage(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long partId) {
        IPage<PartOutbound> page = outboundService.page(pageNum, pageSize, partId);
        PageResult<PartOutbound> result = PageResult.of(
                page.getTotal(),
                page.getCurrent(),
                page.getSize(),
                page.getRecords()
        );
        return Result.success(result);
    }

    /**
     * 新增出库
     */
    @PostMapping("/outbound")
    public Result<PartOutbound> outbound(@RequestBody PartOutbound outbound) {
        return Result.success(outboundService.add(outbound));
    }
}