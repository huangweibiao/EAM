package com.eam.controller;

import org.springframework.data.domain.Page;
import com.eam.security.RequirePermission;
import com.eam.common.PageResult;
import com.eam.common.Result;
import com.eam.entity.PurchaseOrder;
import com.eam.entity.PurchaseRequest;
import com.eam.entity.Supplier;
import com.eam.service.IPurchaseOrderService;
import com.eam.service.IPurchaseRequestService;
import com.eam.service.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 采购管理 Controller
 */
@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {

    @Autowired
    private IPurchaseRequestService requestService;
    @Autowired
    private IPurchaseOrderService orderService;
    @Autowired
    private ISupplierService supplierService;

    // ========== 采购申请 ==========

    /**
     * 分页查询采购申请
     */
    @GetMapping("/request/page")
    @RequirePermission("purchase:request:list")
    public Result<PageResult<PurchaseRequest>> requestPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status) {
        Page<PurchaseRequest> page = requestService.page((long)pageNum, (long)pageSize, status);
        PageResult<PurchaseRequest> result = PageResult.of(
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getContent()
        );
        return Result.success(result);
    }

    /**
     * 获取所有采购申请
     */
    @GetMapping("/request/list")
    @RequirePermission("purchase:request:list")
    public Result<?> requestList() {
        return Result.success(requestService.list());
    }

    /**
     * 根据ID获取采购申请
     */
    @GetMapping("/request/{id}")
    @RequirePermission("purchase:request:list")
    public Result<PurchaseRequest> getRequestById(@PathVariable Long id) {
        return Result.success(requestService.getById(id));
    }

    /**
     * 新增采购申请
     */
    @PostMapping("/request/add")
    @RequirePermission("purchase:request:add")
    public Result<PurchaseRequest> addRequest(@RequestBody PurchaseRequest request) {
        return Result.success(requestService.add(request));
    }

    /**
     * 审批采购申请
     */
    @PostMapping("/request/approve")
    @RequirePermission("purchase:request:approve")
    public Result<PurchaseRequest> approveRequest(@RequestParam Long id, @RequestParam String approver,
                                                    @RequestParam boolean approved, @RequestParam(required = false) String remark) {
        return Result.success(requestService.approve(id, approver, approved, remark));
    }

    /**
     * 删除采购申请
     */
    @DeleteMapping("/request/{id}")
    @RequirePermission("purchase:request:delete")
    public Result<?> deleteRequest(@PathVariable Long id) {
        return Result.success(requestService.delete(id));
    }

    // ========== 采购订单 ==========

    /**
     * 分页查询采购订单
     */
    @GetMapping("/order/page")
    @RequirePermission("purchase:order:list")
    public Result<PageResult<PurchaseOrder>> orderPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status) {
        Page<PurchaseOrder> page = orderService.page((long)pageNum, (long)pageSize, status);
        PageResult<PurchaseOrder> result = PageResult.of(
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getContent()
        );
        return Result.success(result);
    }

    /**
     * 获取所有采购订单
     */
    @GetMapping("/order/list")
    @RequirePermission("purchase:order:list")
    public Result<?> orderList() {
        return Result.success(orderService.list());
    }

    /**
     * 根据ID获取采购订单
     */
    @GetMapping("/order/{id}")
    @RequirePermission("purchase:order:list")
    public Result<PurchaseOrder> getOrderById(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    /**
     * 新增采购订单
     */
    @PostMapping("/order/add")
    @RequirePermission("purchase:order:add")
    public Result<PurchaseOrder> addOrder(@RequestBody PurchaseOrder order) {
        return Result.success(orderService.add(order));
    }

    /**
     * 收货确认
     */
    @PostMapping("/order/receive")
    @RequirePermission("purchase:order:receive")
    public Result<PurchaseOrder> receiveOrder(@RequestParam Long id) {
        return Result.success(orderService.receive(id));
    }

    /**
     * 删除采购订单
     */
    @DeleteMapping("/order/{id}")
    @RequirePermission("purchase:order:delete")
    public Result<?> deleteOrder(@PathVariable Long id) {
        return Result.success(orderService.delete(id));
    }

    // ========== 供应商 ==========

    /**
     * 获取所有供应商
     */
    @GetMapping("/supplier/list")
    @RequirePermission("supplier:list")
    public Result<?> supplierList() {
        return Result.success(supplierService.listAll());
    }

    /**
     * 根据ID获取供应商
     */
    @GetMapping("/supplier/{id}")
    @RequirePermission("supplier:list")
    public Result<Supplier> getSupplierById(@PathVariable Long id) {
        return Result.success(supplierService.getById(id));
    }

    /**
     * 新增供应商
     */
    @PostMapping("/supplier/add")
    @RequirePermission("supplier:add")
    public Result<Supplier> addSupplier(@RequestBody Supplier supplier) {
        return Result.success(supplierService.add(supplier));
    }

    /**
     * 修改供应商
     */
    @PostMapping("/supplier/update")
    @RequirePermission("supplier:update")
    public Result<Supplier> updateSupplier(@RequestBody Supplier supplier) {
        return Result.success(supplierService.update(supplier));
    }

    /**
     * 删除供应商
     */
    @DeleteMapping("/supplier/{id}")
    @RequirePermission("supplier:delete")
    public Result<?> deleteSupplier(@PathVariable Long id) {
        return Result.success(supplierService.delete(id));
    }
}
