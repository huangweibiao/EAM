package com.eam.controller;

import org.springframework.data.domain.Page;
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

    private final IPurchaseRequestService requestService;
    private final IPurchaseOrderService orderService;
    private final ISupplierService supplierService;

    @Autowired
    public PurchaseController(IPurchaseRequestService requestService,
                            IPurchaseOrderService orderService,
                            ISupplierService supplierService) {
        this.requestService = requestService;
        this.orderService = orderService;
        this.supplierService = supplierService;
    }

    // ========== 采购申请 ==========

    @GetMapping("/request/page")
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

    @GetMapping("/request/list")
    public Result<?> requestList() {
        return Result.success(requestService.list());
    }

    @PostMapping("/request/add")
    public Result<PurchaseRequest> addRequest(@RequestBody PurchaseRequest request) {
        return Result.success(requestService.add(request));
    }

    @GetMapping("/request/{id}")
    public Result<PurchaseRequest> getRequestById(@PathVariable Long id) {
        return Result.success(requestService.getById(id));
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

    @GetMapping("/order/list")
    public Result<?> orderList() {
        return Result.success(orderService.list());
    }

    @PostMapping("/order/add")
    public Result<PurchaseOrder> addOrder(@RequestBody PurchaseOrder order) {
        return Result.success(orderService.add(order));
    }

    @GetMapping("/order/{id}")
    public Result<PurchaseOrder> getOrderById(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    @PostMapping("/order/receive")
    public Result<PurchaseOrder> receiveOrder(@PathVariable Long id) {
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

    @GetMapping("/supplier/{id}")
    public Result<Supplier> getSupplierById(@PathVariable Long id) {
        return Result.success(supplierService.getById(id));
    }

    @PostMapping("/supplier/add")
    public Result<Supplier> addSupplier(@RequestBody Supplier supplier) {
        return Result.success(supplierService.add(supplier));
    }

    @PostMapping("/supplier/update")
    public Result<Supplier> updateSupplier(@RequestBody Supplier supplier) {
        return Result.success(supplierService.update(supplier));
    }

    @DeleteMapping("/supplier/{id}")
    public Result<?> deleteSupplier(@PathVariable Long id) {
        return Result.success(supplierService.delete(id));
    }
}