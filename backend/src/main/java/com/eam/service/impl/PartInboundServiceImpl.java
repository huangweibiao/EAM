package com.eam.service.impl;

import com.eam.entity.PartInbound;
import com.eam.repository.PartInboundRepository;
import com.eam.service.IPartInboundService;
import com.eam.service.ISparePartService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * 备件入库 Service 实现类
 */
@Service
public class PartInboundServiceImpl implements IPartInboundService {

    private final PartInboundRepository partInboundRepository;
    private final ISparePartService sparePartService;

    @Autowired
    public PartInboundServiceImpl(PartInboundRepository partInboundRepository,
                                   ISparePartService sparePartService) {
        this.partInboundRepository = partInboundRepository;
        this.sparePartService = sparePartService;
    }

    @Override
    public Page<PartInbound> page(Long pageNum, Long pageSize, Long partId) {
        // JPA 分页从 0 开始，MyBatis-Plus 从 1 开始，需要转换
        Pageable pageable = PageRequest.of(pageNum.intValue() - 1, pageSize.intValue(),
                Sort.by(Sort.Direction.DESC, "inboundDate"));

        Specification<PartInbound> spec = (root, query, cb) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();
            if (partId != null) {
                predicates.add(cb.equal(root.get("partId"), partId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return partInboundRepository.findAll(spec, pageable);
    }

    @Override
    public PartInbound add(PartInbound inbound) {
        // 生成入库单号
        String inboundNo = "IN" + System.currentTimeMillis();
        inbound.setInboundNo(inboundNo);
        if (inbound.getInboundDate() == null) {
            inbound.setInboundDate(LocalDateTime.now());
        }
        if (inbound.getTotalAmount() == null && inbound.getQuantity() != null && inbound.getUnitPrice() != null) {
            inbound.setTotalAmount(inbound.getQuantity().multiply(inbound.getUnitPrice()));
        }

        partInboundRepository.save(inbound);

        // 更新备件库存（入库后增加库存）
        sparePartService.updateQuantity(inbound.getPartId(), inbound.getQuantity());

        return inbound;
    }
}