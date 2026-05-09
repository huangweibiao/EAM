package com.eam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eam.entity.PurchaseRequest;

/**
 * 采购申请 Service 接口
 */
public interface IPurchaseRequestService extends IService<PurchaseRequest> {

    IPage<PurchaseRequest> page(Long pageNum, Long pageSize, String status);

    PurchaseRequest add(PurchaseRequest request);

    PurchaseRequest approve(Long id, String approver, boolean approved, String remark);

    boolean delete(Long id);
}
