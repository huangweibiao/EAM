package com.eam.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.eam.common.PageResult;
import com.eam.common.Result;
import com.eam.entity.WorkOrder;
import com.eam.service.IWorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 工单管理 Controller
 */
@RestController
@RequestMapping("/api/workorder")
public class WorkOrderController {

    @Autowired
    private IWorkOrderService workOrderService;

    /**
     * 分页查询工单
     */
    @GetMapping("/page")
    public Result<PageResult<WorkOrder>> page(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String orderType) {
        IPage<WorkOrder> page = workOrderService.page(pageNum, pageSize, keyword, status, orderType);
        PageResult<WorkOrder> result = PageResult.of(
                page.getTotal(),
                page.getCurrent(),
                page.getSize(),
                page.getRecords()
        );
        return Result.success(result);
    }

    /**
     * 获取所有工单
     */
    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(workOrderService.list());
    }

    /**
     * 根据ID获取工单
     */
    @GetMapping("/{id}")
    public Result<WorkOrder> getById(@PathVariable Long id) {
        return Result.success(workOrderService.getById(id));
    }

    /**
     * 创建工单
     */
    @PostMapping("/create")
    public Result<WorkOrder> create(@RequestBody WorkOrder workOrder) {
        return Result.success(workOrderService.create(workOrder));
    }

    /**
     * 指派工单
     */
    @PostMapping("/assign")
    public Result<WorkOrder> assign(@RequestParam Long id, @RequestParam String assignedTo) {
        return Result.success(workOrderService.assign(id, assignedTo));
    }

    /**
     * 开始处理
     */
    @PostMapping("/process")
    public Result<WorkOrder> process(@RequestParam Long id) {
        return Result.success(workOrderService.process(id));
    }

    /**
     * 完成工单
     */
    @PostMapping("/complete")
    public Result<WorkOrder> complete(@RequestParam Long id, @RequestParam(required = false) String solution) {
        return Result.success(workOrderService.complete(id, solution));
    }

    /**
     * 关闭工单
     */
    @PostMapping("/close")
    public Result<WorkOrder> close(@RequestParam Long id) {
        return Result.success(workOrderService.close(id));
    }

    /**
     * 评价工单
     */
    @PostMapping("/rate")
    public Result<WorkOrder> rate(@RequestParam Long id, @RequestParam Integer rating) {
        return Result.success(workOrderService.rate(id, rating));
    }
}