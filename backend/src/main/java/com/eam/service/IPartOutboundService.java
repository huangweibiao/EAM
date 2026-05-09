package com.eam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eam.entity.PartOutbound;

/**
 * 备件出库 Service 接口
 */
public interface IPartOutboundService extends IServiceService<PartOutbound> {

    IPagePage<PartOutbound> page(Long pageNum, Long pageSize, Long partId);

    PartOutbound add(PartOutbound outbound);
}
