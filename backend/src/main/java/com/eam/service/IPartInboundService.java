package com.eam.service;

import com.eam.entity.PartInbound;
import org.springframework.data.domain.Page;

/**
 * 备件入库 Service 接口
 */
public interface IPartInboundService {

    Page<PartInbound> page(Long pageNum, Long pageSize, Long partId);

    PartInbound add(PartInbound inbound);
}
