package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.PurchaseOrder;
import com.eam.entity.PurchaseRequest;
import com.eam.repository.PurchaseOrderRepository;
import com.eam.repository.PurchaseRequestRepository;
import com.eam.service.IPurchaseOrderService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 采购订单 Service 实现类
 */
@Service
public class PurchaseOrderServiceImpl implements IPurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;

    @Autowired
    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository,
                                     PurchaseRequestRepository purchaseRequestRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseRequestRepository = purchaseRequestRepository;
    }

    @Override
    public Page<PurchaseOrder> page(Long pageNum, Long pageSize, String status) {
        // JPA 分页从 0 开始，MyBatis-Plus 从 1 开始，需要转换
        Pageable pageable = PageRequest.of(pageNum.intValue() - 1, pageSize.intValue(),
                Sort.by(Sort.Direction.DESC, "createTime"));

        Specification<PurchaseOrder> spec = (root, query, cb) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return purchaseOrderRepository.findAll(spec, pageable);
    }

    @Override
    public PurchaseOrder add(PurchaseOrder order) {
        // 校验采购订单数量不能大于采购申请数量
        if (order.getRequestId() != null) {
            PurchaseRequest request = purchaseRequestRepository.findById(order.getRequestId()).orElse(null);
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
        return purchaseOrderRepository.save(order);
    }

    @Override
    public PurchaseOrder receive(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id).orElse(null);
        if (order == null) {
            throw new BusinessException("采购订单不存在");
        }
        order.setStatus("RECEIVED");
        order.setActualDeliveryDate(LocalDate.now());
        return purchaseOrderRepository.save(order);
    }

    @Override
    public boolean delete(Long id) {
        purchaseOrderRepository.deleteById(id);
        return true;
    }

    @Override
    public PurchaseOrder getById(Long id) {
        return purchaseOrderRepository.findById(id).orElse(null);
    }

    @Override
    public List<PurchaseOrder> list() {
        return purchaseOrderRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
    }
}