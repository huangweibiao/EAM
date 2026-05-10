package com.eam.service;

import com.eam.entity.PurchaseRequest;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 采购申请 Service 接口
 */
public interface IPurchaseRequestService {

    Page<PurchaseRequest> page(Long pageNum, Long pageSize, String status);

    PurchaseRequest add(PurchaseRequest request);

    PurchaseRequest approve(Long id, String approver, boolean approved, String remark);

    boolean delete(Long id);

    PurchaseRequest getById(Long id);

    List<PurchaseRequest> list();
}
