package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.PartInbound;
import com.eam.entity.PurchaseOrder;
import com.eam.entity.PurchaseRequest;
import com.eam.repository.PartInboundRepository;
import com.eam.repository.PurchaseOrderRepository;
import com.eam.repository.PurchaseRequestRepository;
import com.eam.service.IPartInboundService;
import com.eam.service.IPurchaseOrderService;
import com.eam.service.IPurchaseRequestService;
import com.eam.service.ISparePartService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final PartInboundRepository partInboundRepository;
    private final IPartInboundService partInboundService;
    private final ISparePartService sparePartService;
    private final IPurchaseRequestService purchaseRequestService;

    @Autowired
    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository,
                                      PurchaseRequestRepository purchaseRequestRepository,
                                      PartInboundRepository partInboundRepository,
                                      IPartInboundService partInboundService,
                                      ISparePartService sparePartService,
                                      IPurchaseRequestService purchaseRequestService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseRequestRepository = purchaseRequestRepository;
        this.partInboundRepository = partInboundRepository;
        this.partInboundService = partInboundService;
        this.sparePartService = sparePartService;
        this.purchaseRequestService = purchaseRequestService;
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
    @Transactional
    public PurchaseOrder receive(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id).orElse(null);
        if (order == null) {
            throw new BusinessException("采购订单不存在");
        }
        
        // 验证订单状态和收货条件
        if (!"APPROVED".equals(order.getStatus())) {
            throw new BusinessException("只有已审批的采购订单可以收货");
        }
        if ("RECEIVED".equals(order.getStatus())) {
            throw new BusinessException("该订单已经收货，不能重复收货");
        }

        // 创建PartInbound记录
        PartInbound inbound = new PartInbound();
        inbound.setPartId(order.getPartId());
        inbound.setPurchaseOrderId(order.getId());
        inbound.setQuantity(order.getOrderQuantity());
        inbound.setUnitPrice(order.getUnitPrice());
        
        // 计算总金额
        if (order.getTotalAmount() != null) {
            inbound.setTotalAmount(order.getTotalAmount());
        } else {
            inbound.setTotalAmount(order.getOrderQuantity().multiply(order.getUnitPrice()));
        }
        
        // 设置入库日期和供应商信息
        inbound.setInboundDate(LocalDateTime.now());
        if (order.getSupplierId() != null) {
            inbound.setSupplierId(order.getSupplierId());
        }
        
        // 保存入库记录
        PartInbound savedInbound = partInboundRepository.save(inbound);
        
        // 更新备件库存数量
        if (order.getPartId() != null && order.getOrderQuantity() != null) {
            try {
                sparePartService.updateQuantity(order.getPartId(), order.getOrderQuantity());
            } catch (Exception e) {
                // 库存更新异常，不影响订单收货流程
                System.err.println("更新备件库存失败: " + e.getMessage());
            }
        }
        
        // 更新采购申请状态
        if (order.getRequestId() != null) {
            try {
                purchaseRequestService.approve(
                    order.getRequestId(),
                    order.getOperator() != null ? order.getOperator() : "system",
                    true,
                    "采购订单已收货"
                );
            } catch (Exception e) {
                // 采购申请状态更新异常，不影响订单收货流程
                System.err.println("更新采购申请状态失败: " + e.getMessage());
            }
        }
        
        // 更新订单状态
        order.setActualDeliveryDate(LocalDate.now());
        order.setStatus("RECEIVED");
        
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