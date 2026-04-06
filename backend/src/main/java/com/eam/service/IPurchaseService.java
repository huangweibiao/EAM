package com.eam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eam.entity.PurchaseRequest;
import com.eam.entity.PurchaseOrder;

import java.util.List;

/**
 * 采购申请 Service 接口
 */
public interface IPurchaseRequestService extends IService<PurchaseRequest> {

    IPage<PurchaseRequest> page(Long pageNum, Long pageSize, String status);

    PurchaseRequest add(PurchaseRequest request);

    PurchaseRequest approve(Long id, String approver, boolean approved, String remark);

    boolean delete(Long id);
}

/**
 * 采购订单 Service 接口
 */
public interface IPurchaseOrderService extends IService<PurchaseOrder> {

    IPage<PurchaseOrder> page(Long pageNum, Long pageSize, String status);

    PurchaseOrder add(PurchaseOrder order);

    PurchaseOrder receive(Long id);

    boolean delete(Long id);
}

/**
 * 供应商 Service 接口
 */
public interface ISupplierService extends IService<com.eam.entity.Supplier> {

    List<com.eam.entity.Supplier> listAll();

    com.eam.entity.Supplier add(com.eam.entity.Supplier supplier);

    com.eam.entity.Supplier update(com.eam.entity.Supplier supplier);

    boolean delete(Long id);
}