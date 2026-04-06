package com.eam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eam.entity.PartInbound;
import com.eam.entity.PartOutbound;

import java.util.List;

/**
 * 备件入库 Service 接口
 */
public interface IPartInboundService extends IService<PartInbound> {

    IPage<PartInbound> page(Long pageNum, Long pageSize, Long partId);

    PartInbound add(PartInbound inbound);
}

/**
 * 备件出库 Service 接口
 */
public interface IPartOutboundService extends IService<PartOutbound> {

    IPage<PartOutbound> page(Long pageNum, Long pageSize, Long partId);

    PartOutbound add(PartOutbound outbound);
}