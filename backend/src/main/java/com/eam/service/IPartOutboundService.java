package com.eam.service;

import com.eam.entity.PartOutbound;
import org.springframework.data.domain.Page;

/**
 * 备件出库 Service 接口
 */
public interface IPartOutboundService {

    Page<PartOutbound> page(Long pageNum, Long pageSize, Long partId);

    PartOutbound add(PartOutbound outbound);
}
