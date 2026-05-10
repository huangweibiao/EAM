package com.eam.controller;

import org.springframework.data.domain.Page;
import com.eam.annotation.OperationLog;
import com.eam.security.RequirePermission;
import com.eam.common.PageResult;
import com.eam.common.Result;
import com.eam.entity.WorkOrder;
import com.eam.entity.WorkOrderPart;
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
    @RequirePermission("workorder:list")
    public Result<PageResult<WorkOrder>> page(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String orderType) {
        Page<WorkOrder> page = workOrderService.page(pageNum, pageSize, keyword, status, orderType);
        PageResult<WorkOrder> result = PageResult.of(
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getContent()
        );
        return Result.success(result);
    }

    /**
     * 获取所有工单
     */
    @GetMapping("/list")
    @RequirePermission("workorder:list")
    public Result<?> list() {
        return Result.success(workOrderService.list());
    }

    /**
     * 根据ID获取工单
     */
    @GetMapping("/{id}")
    @RequirePermission("workorder:list")
    public Result<WorkOrder> getById(@PathVariable Long id) {
        return Result.success(workOrderService.getById(id));
    }

    /**
     * 创建工单
     */
    @PostMapping("/create")
    @RequirePermission("workorder:create")
    @OperationLog(value = "创建工单", description = "创建新的工单", operationType = "CREATE", recordParams = true, recordResult = true)
    public Result<WorkOrder> create(@RequestBody WorkOrder workOrder) {
        return Result.success(workOrderService.create(workOrder));
    }

    /**
     * 指派工单
     */
    @PostMapping("/assign")
    @RequirePermission("workorder:assign")
    @OperationLog(value = "指派工单", description = "将工单指派给处理人员", operationType = "UPDATE", recordParams = true, recordResult = true)
    public Result<WorkOrder> assign(@RequestParam Long id, @RequestParam String assignedTo) {
        return Result.success(workOrderService.assign(id, assignedTo));
    }

    /**
     * 开始处理
     */
    @PostMapping("/process")
    @RequirePermission("workorder:process")
    @OperationLog(value = "开始处理工单", description = "开始处理工单", operationType = "UPDATE", recordParams = true, recordResult = true)
    public Result<WorkOrder> process(@RequestParam Long id) {
        return Result.success(workOrderService.process(id));
    }

    /**
     * 完成工单
     */
    @PostMapping("/complete")
    @RequirePermission("workorder:complete")
    @OperationLog(value = "完成工单", description = "完成工单处理", operationType = "UPDATE", recordParams = true, recordResult = true)
    public Result<WorkOrder> complete(@RequestParam Long id, @RequestParam(required = false) String solution) {
        return Result.success(workOrderService.complete(id, solution));
    }

    /**
     * 关闭工单
     */
    @PostMapping("/close")
    @RequirePermission("workorder:close")
    @OperationLog(value = "关闭工单", description = "关闭工单", operationType = "UPDATE", recordParams = true, recordResult = true)
    public Result<WorkOrder> close(@RequestParam Long id) {
        return Result.success(workOrderService.close(id));
    }

    /**
     * 评价工单
     */
    @PostMapping("/rate")
    @RequirePermission("workorder:rate")
    @OperationLog(value = "评价工单", description = "评价工单服务质量", operationType = "UPDATE", recordParams = true, recordResult = true)
    public Result<WorkOrder> rate(@RequestParam Long id, @RequestParam Integer rating) {
        return Result.success(workOrderService.rate(id, rating));
    }

    /**
     * 取消工单
     */
    @PostMapping("/cancel")
    @RequirePermission("workorder:cancel")
    @OperationLog(value = "取消工单", description = "取消工单处理", operationType = "UPDATE", recordParams = true, recordResult = true)
    public Result<WorkOrder> cancel(@RequestParam Long id,
                                      @RequestParam(required = false) String cancelReason,
                                      @RequestParam(required = false) String cancelBy) {
        return Result.success(workOrderService.cancel(id, cancelReason, cancelBy));
    }

    // ============================================
    // 工单-备件关联相关接口
    // ============================================

    /**
     * 获取工单的备件列表
     */
    @GetMapping("/{workOrderId}/parts")
    @RequirePermission("workorder:list")
    public Result<List<WorkOrderPart>> getPartsByWorkOrder(@PathVariable Long workOrderId) {
        return Result.success(workOrderService.getPartsByWorkOrder(workOrderId));
    }

    /**
     * 添加工单备件关联
     */
    @PostMapping("/{workOrderId}/parts")
    @RequirePermission("workorder:part:add")
    public Result<WorkOrderPart> addPart(@PathVariable Long workOrderId, @RequestBody WorkOrderPart workOrderPart) {
        workOrderPart.setWorkOrderId(workOrderId);
        return Result.success(workOrderService.addPart(workOrderPart));
    }

    /**
     * 更新工单备件消耗量
     */
    @PutMapping("/{workOrderId}/parts/{partId}")
    @RequirePermission("workorder:part:update")
    public Result<Boolean> updatePartConsumption(
            @PathVariable Long workOrderId,
            @PathVariable Long partId,
            @RequestParam BigDecimal actualQuantity) {
        boolean result = workOrderService.updatePartConsumption(workOrderId, partId, actualQuantity);
        return Result.success(result);
    }

    /**
     * 删除工单备件关联
     */
    @DeleteMapping("/{workOrderId}/parts/{partId}")
    @RequirePermission("workorder:part:delete")
    public Result<Boolean> removePart(@PathVariable Long workOrderId, @PathVariable Long partId) {
        boolean result = workOrderService.removePartFromWorkOrder(workOrderId, partId);
        return Result.success(result);
    }
}