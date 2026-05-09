package com.eam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eam.entity.PurchaseOrder;

/**
 * 采购订单 Service 接口
 */
public interface IPurchaseOrderService extends IService<PurchaseOrder> {

    IPage<PurchaseOrder> page(Long pageNum, Long pageSize, String status);

    PurchaseOrder add(PurchaseOrder order);

    PurchaseOrder receive(Long id);

    boolean delete(Long id);
}
