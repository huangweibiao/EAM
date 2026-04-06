package com.eam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eam.entity.PartOutbound;
import org.apache.ibatis.annotations.Mapper;

/**
 * 备件出库记录 Mapper
 */
@Mapper
public interface PartOutboundMapper extends BaseMapper<PartOutbound> {
}