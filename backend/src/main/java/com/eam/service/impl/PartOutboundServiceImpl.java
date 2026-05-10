package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.PartOutbound;
import com.eam.entity.PurchaseRequest;
import com.eam.entity.SparePart;
import com.eam.repository.PartOutboundRepository;
import com.eam.service.IPartOutboundService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * 备件出库 Service 实现类
 */
@Service
public class PartOutboundServiceImpl implements IPartOutboundService {

    private final PartOutboundRepository partOutboundRepository;
    private final ISparePartService sparePartService;
    private final IPurchaseRequestService purchaseRequestService;

    @Autowired
    public PartOutboundServiceImpl(PartOutboundRepository partOutboundRepository,
                                    ISparePartService sparePartService,
                                    IPurchaseRequestService purchaseRequestService) {
        this.partOutboundRepository = partOutboundRepository;
        this.sparePartService = sparePartService;
        this.purchaseRequestService = purchaseRequestService;
    }

    @Override
    public Page<PartOutbound> page(Long pageNum, Long pageSize, Long partId) {
        // JPA 分页从 0 开始，MyBatis-Plus 从 1 开始，需要转换
        Pageable pageable = PageRequest.of(pageNum.intValue() - 1, pageSize.intValue(),
                Sort.by(Sort.Direction.DESC, "outboundDate"));

        Specification<PartOutbound> spec = (root, query, cb) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();
            if (partId != null) {
                predicates.add(cb.equal(root.get("partId"), partId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return partOutboundRepository.findAll(spec, pageable);
    }

    @Override
    public PartOutbound add(PartOutbound outbound) {
        // 生成出库单号
        String outboundNo = "OUT" + System.currentTimeMillis();
        outbound.setOutboundNo(outboundNo);
        if (outbound.getOutboundDate() == null) {
            outbound.setOutboundDate(LocalDateTime.now());
        }
        if (outbound.getTotalAmount() == null && outbound.getQuantity() != null && outbound.getUnitPrice() != null) {
            outbound.setTotalAmount(outbound.getQuantity().multiply(outbound.getUnitPrice()));
        }

        // 检查库存是否充足（出库数量 <= 当前库存）
        SparePart sparePart = sparePartService.getById(outbound.getPartId());
        if (sparePart == null) {
            throw new BusinessException("备件不存在");
        }
        if (sparePart.getQuantity().compareTo(outbound.getQuantity()) < 0) {
            throw new BusinessException("库存不足，当前库存：" + sparePart.getQuantity() + "，出库数量：" + outbound.getQuantity());
        }

        partOutboundRepository.save(outbound);

        // 扣减库存(出库后减少库存)
        sparePartService.updateQuantity(outbound.getPartId(), outbound.getQuantity().negate());

        // 检查库存是否低于最低库存预警量，如果是则自动创建采购申请
        checkAndCreatePurchaseRequest(sparePart, outbound.getCreateBy());

        return outbound;
    }

    /**
     * 检查库存并自动创建采购申请
     * 规则：当备件库存 < 最低库存量时，自动创建采购申请
     */
    private void checkAndCreatePurchaseRequest(SparePart sparePart, String operator) {
        if (sparePart == null || sparePart.getMinQuantity() == null) {
            return;
        }

        // 重新查询最新的库存数量
        SparePart latestPart = sparePartService.getById(sparePart.getId());
        if (latestPart == null || latestPart.getQuantity() == null) {
            return;
        }

        // 如果库存低于最低库存预警量
        if (latestPart.getQuantity().compareTo(latestPart.getMinQuantity()) < 0) {
            // 计算建议采购数量：最高库存 - 当前库存
            BigDecimal suggestQuantity = BigDecimal.valueOf(10); // 默认采购10个
            if (latestPart.getMaxQuantity() != null && latestPart.getMaxQuantity().compareTo(BigDecimal.ZERO) > 0) {
                suggestQuantity = latestPart.getMaxQuantity().subtract(latestPart.getQuantity());
            }

            if (suggestQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                suggestQuantity = BigDecimal.valueOf(10);
            }

            // 创建采购申请
            PurchaseRequest request = new PurchaseRequest();
            request.setPartId(latestPart.getId());
            request.setRequestQuantity(suggestQuantity);
            request.setEstimatedPrice(latestPart.getUnitPrice());
            if (latestPart.getUnitPrice() != null) {
                request.setTotalEstimatedAmount(suggestQuantity.multiply(latestPart.getUnitPrice()));
            }
            request.setUrgency("NORMAL");
            request.setExpectedDate(java.time.LocalDate.now().plusDays(7)); // 期望7天后到货
            request.setReason("库存预警自动创建：当前库存 " + latestPart.getQuantity() + "，低于最低库存 " + latestPart.getMinQuantity());
            request.setRequester(operator != null ? operator : "SYSTEM");
            request.setRequestTime(java.time.LocalDateTime.now());
            request.setApproveStatus("PENDING");
            request.setStatus("ACTIVE");

            purchaseRequestService.add(request);
        }
    }
}