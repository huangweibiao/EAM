package com.eam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eam.entity.PartInbound;
import org.apache.ibatis.annotations.Mapper;

/**
 * 备件入库记录 Mapper
 */
@Mapper
public interface PartInboundMapper extends BaseMapper<PartInbound> {
}