package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eam.common.BusinessException;
import com.eam.entity.PurchaseOrder;
import com.eam.entity.PurchaseRequest;
import com.eam.entity.Supplier;
import com.eam.mapper.PurchaseOrderMapper;
import com.eam.mapper.PurchaseRequestMapper;
import com.eam.mapper.SupplierMapper;
import com.eam.service.IPurchaseRequestService;
import com.eam.service.IPurchaseOrderService;
import com.eam.service.ISupplierService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

/**
 * 采购订单 Service 实现类
 */
@Service
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements IPurchaseOrderService {

    @Override
    public IPage<PurchaseOrder> page(Long pageNum, Long pageSize, String status) {
        Page<PurchaseOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(PurchaseOrder::getStatus, status);
        }
        wrapper.orderByDesc(PurchaseOrder::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public PurchaseOrder add(PurchaseOrder order) {
        String orderNo = "PO" + System.currentTimeMillis();
        order.setOrderNo(orderNo);
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }
        if (order.getStatus() == null) {
            order.setStatus("PENDING");
        }
        if (order.getPaymentStatus() == null) {
            order.setPaymentStatus("UNPAID");
        }
        if (order.getTotalAmount() == null && order.getOrderQuantity() != null && order.getUnitPrice() != null) {
            order.setTotalAmount(order.getOrderQuantity().multiply(order.getUnitPrice()));
        }
        this.save(order);
        return order;
    }

    @Override
    public PurchaseOrder receive(Long id) {
        PurchaseOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("采购订单不存在");
        }
        order.setStatus("RECEIVED");
        order.setActualDeliveryDate(LocalDate.now());
        this.updateById(order);
        return order;
    }

    @Override
    public boolean delete(Long id) {
        return this.removeById(id);
    }
}

/**
 * 供应商 Service 实现类
 */
@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements ISupplierService {

    @Override
    public List<Supplier> listAll() {
        return this.list();
    }

    @Override
    public Supplier add(Supplier supplier) {
        Long count = this.count(new LambdaQueryWrapper<Supplier>()
                .eq(Supplier::getSupplierCode, supplier.getSupplierCode()));
        if (count > 0) {
            throw new BusinessException("供应商编码已存在");
        }
        if (supplier.getCooperationStatus() == null) {
            supplier.setCooperationStatus("ACTIVE");
        }
        this.save(supplier);
        return supplier;
    }

    @Override
    public Supplier update(Supplier supplier) {
        if (supplier.getId() == null) {
            throw new BusinessException("供应商ID不能为空");
        }
        this.updateById(supplier);
        return supplier;
    }

    @Override
    public boolean delete(Long id) {
        return this.removeById(id);
    }
}