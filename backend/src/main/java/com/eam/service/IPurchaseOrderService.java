package com.eam.service;

import com.eam.entity.PurchaseOrder;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 采购订单 Service 接口
 */
public interface IPurchaseOrderService {

    Page<PurchaseOrder> page(Long pageNum, Long pageSize, String status);

    PurchaseOrder add(PurchaseOrder order);

    PurchaseOrder receive(Long id);

    boolean delete(Long id);

    PurchaseOrder getById(Long id);

    List<PurchaseOrder> list();
}
