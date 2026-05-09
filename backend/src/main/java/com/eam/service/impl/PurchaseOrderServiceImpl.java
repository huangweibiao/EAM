package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eam.common.BusinessException;
import com.eam.entity.PurchaseOrder;
import com.eam.entity.PurchaseRequest;
import com.eam.mapper.PurchaseOrderMapper;
import com.eam.service.IPurchaseOrderService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
        // 校验采购订单数量不能大于采购申请数量
        if (order.getRequestId() != null) {
            PurchaseRequest request = this.getBaseMapper().selectById(order.getRequestId());
            if (request != null && order.getOrderQuantity() != null && request.getRequestQuantity() != null) {
                if (order.getOrderQuantity().compareTo(request.getRequestQuantity()) > 0) {
                    throw new BusinessException("采购订单数量不能大于采购申请数量");
                }
            }
        }

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
