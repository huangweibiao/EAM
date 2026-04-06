package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eam.entity.PartInbound;
import com.eam.entity.SparePart;
import com.eam.mapper.PartInboundMapper;
import com.eam.service.IPartInboundService;
import com.eam.service.ISparePartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 备件入库 Service 实现类
 */
@Service
public class PartInboundServiceImpl extends ServiceImpl<PartInboundMapper, PartInbound> implements IPartInboundService {

    @Autowired
    private ISparePartService sparePartService;

    @Override
    public IPage<PartInbound> page(Long pageNum, Long pageSize, Long partId) {
        Page<PartInbound> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PartInbound> wrapper = new LambdaQueryWrapper<>();
        if (partId != null) {
            wrapper.eq(PartInbound::getPartId, partId);
        }
        wrapper.orderByDesc(PartInbound::getInboundDate);
        return this.page(page, wrapper);
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

        this.save(inbound);

        // 更新备件库存
        sparePartService.updateQuantity(inbound.getPartId(), inbound.getQuantity());

        return inbound;
    }
}

/**
 * 备件出库 Service 实现类
 */
@Service
public class PartOutboundServiceImpl extends ServiceImpl<com.eam.mapper.PartOutboundMapper, PartOutbound> implements IPartOutboundService {

    @Autowired
    private ISparePartService sparePartService;

    @Override
    public IPage<PartOutbound> page(Long pageNum, Long pageSize, Long partId) {
        Page<PartOutbound> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PartOutbound> wrapper = new LambdaQueryWrapper<>();
        if (partId != null) {
            wrapper.eq(PartOutbound::getPartId, partId);
        }
        wrapper.orderByDesc(PartOutbound::getOutboundDate);
        return this.page(page, wrapper);
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

        // 检查库存是否充足
        SparePart sparePart = sparePartService.getById(outbound.getPartId());
        if (sparePart == null) {
            throw new RuntimeException("备件不存在");
        }
        if (sparePart.getQuantity().compareTo(outbound.getQuantity()) < 0) {
            throw new RuntimeException("库存不足");
        }

        this.save(outbound);

        // 扣减库存(负数)
        sparePartService.updateQuantity(outbound.getPartId(), outbound.getQuantity().negate());

        return outbound;
    }
}