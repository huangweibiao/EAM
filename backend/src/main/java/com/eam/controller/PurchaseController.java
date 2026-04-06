package com.eam.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
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

    @GetMapping("/request/page")
    public Result<PageResult<PurchaseRequest>> requestPage(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String status) {
        IPage<PurchaseRequest> page = requestService.page(pageNum, pageSize, status);
        return Result.success(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
    }

    @GetMapping("/request/list")
    public Result<?> requestList() {
        return Result.success(requestService.list());
    }

    @PostMapping("/request/add")
    public Result<PurchaseRequest> addRequest(@RequestBody PurchaseRequest request) {
        return Result.success(requestService.add(request));
    }

    @PostMapping("/request/approve")
    public Result<PurchaseRequest> approveRequest(@RequestParam Long id, @RequestParam String approver,
                                                    @RequestParam boolean approved, @RequestParam(required = false) String remark) {
        return Result.success(requestService.approve(id, approver, approved, remark));
    }

    @DeleteMapping("/request/{id}")
    public Result<?> deleteRequest(@PathVariable Long id) {
        return Result.success(requestService.delete(id));
    }

    // ========== 采购订单 ==========

    @GetMapping("/order/page")
    public Result<PageResult<PurchaseOrder>> orderPage(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String status) {
        IPage<PurchaseOrder> page = orderService.page(pageNum, pageSize, status);
        return Result.success(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
    }

    @GetMapping("/order/list")
    public Result<?> orderList() {
        return Result.success(orderService.list());
    }

    @PostMapping("/order/add")
    public Result<PurchaseOrder> addOrder(@RequestBody PurchaseOrder order) {
        return Result.success(orderService.add(order));
    }

    @PostMapping("/order/receive")
    public Result<PurchaseOrder> receiveOrder(@RequestParam Long id) {
        return Result.success(orderService.receive(id));
    }

    @DeleteMapping("/order/{id}")
    public Result<?> deleteOrder(@PathVariable Long id) {
        return Result.success(orderService.delete(id));
    }

    // ========== 供应商 ==========

    @GetMapping("/supplier/list")
    public Result<?> supplierList() {
        return Result.success(supplierService.listAll());
    }

    @PostMapping("/supplier/add")
    public Result<Supplier> addSupplier(@RequestBody Supplier supplier) {
        return Result.success(supplierService.add(supplier));
    }

    @PutMapping("/supplier/update")
    public Result<Supplier> updateSupplier(@RequestBody Supplier supplier) {
        return Result.success(supplierService.update(supplier));
    }

    @DeleteMapping("/supplier/{id}")
    public Result<?> deleteSupplier(@PathVariable Long id) {
        return Result.success(supplierService.delete(id));
    }
}