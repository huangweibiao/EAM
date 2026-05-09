package com.eam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eam.entity.PartInbound;

/**
 * 备件入库 Service 接口
 */
public interface IPartInboundService extends IServiceService<PartInbound> {

    IPagePage<PartInbound> page(Long pageNum, Long pageSize, Long partId);

    PartInbound add(PartInbound inbound);
}
