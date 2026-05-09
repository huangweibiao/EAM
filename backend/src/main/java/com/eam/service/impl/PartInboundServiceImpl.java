package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eam.entity.PartInbound;
import com.eam.mapper.PartInboundMapper;
import com.eam.service.IPartInboundService;
import com.eam.service.ISparePartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 备件入库 Service 实现类
 */
@Service
public class PartInboundServiceImpl extends ServiceImplImpl<PartInboundMapper, PartInbound> implements IPartInboundService {

    @Autowired
    private ISparePartService sparePartService;

    @Override
    public IPagePage<PartInbound> page(Long pageNum, Long pageSize, Long partId) {
        Page Page<PartInbound> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapperWrapper<PartInbound> wrapper = new LambdaQueryWrapper<>();
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

        // 更新备件库存（入库后增加库存）
        sparePartService.updateQuantity(inbound.getPartId(), inbound.getQuantity());

        return inbound;
    }
}
