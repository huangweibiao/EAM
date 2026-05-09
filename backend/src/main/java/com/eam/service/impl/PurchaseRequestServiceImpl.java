package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eam.common.BusinessException;
import com.eam.entity.PurchaseRequest;
import com.eam.mapper.PurchaseRequestMapper;
import com.eam.service.IPurchaseRequestService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 采购申请 Service 实现类
 */
@Service
public class PurchaseRequestServiceImpl extends ServiceImpl<PurchaseRequestMapper, PurchaseRequest> implements IPurchaseRequestService {

    @Override
    public IPage<PurchaseRequest> page(Long pageNum, Long pageSize, String status) {
        Page<PurchaseRequest> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PurchaseRequest> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(PurchaseRequest::getStatus, status);
        }
        wrapper.orderByDesc(PurchaseRequest::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public PurchaseRequest add(PurchaseRequest request) {
        String requestNo = "PR" + System.currentTimeMillis();
        request.setRequestNo(requestNo);
        if (request.getRequestTime() == null) {
            request.setRequestTime(LocalDateTime.now());
        }
        if (request.getStatus() == null) {
            request.setStatus("ACTIVE");
        }
        if (request.getApproveStatus() == null) {
            request.setApproveStatus("PENDING");
        }
        if (request.getUrgency() == null) {
            request.setUrgency("NORMAL");
        }
        this.save(request);
        return request;
    }

    @Override
    public PurchaseRequest approve(Long id, String approver, boolean approved, String remark) {
        PurchaseRequest request = this.getById(id);
        if (request == null) {
            throw new BusinessException("采购申请不存在");
        }
        request.setApprover(approver);
        request.setApproveTime(LocalDateTime.now());
        request.setApproveStatus(approved ? "APPROVED" : "REJECTED");
        request.setApproveRemark(remark);
        if (approved) {
            request.setStatus("APPROVED");
        }
        this.updateById(request);
        return request;
    }

    @Override
    public boolean delete(Long id) {
        return this.removeById(id);
    }
}
