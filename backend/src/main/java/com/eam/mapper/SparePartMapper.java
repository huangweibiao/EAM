package com.eam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eam.entity.SparePart;
import org.apache.ibatis.annotations.Mapper;

/**
 * 备件 Mapper
 */
@Mapper
public interface SparePartMapper extends BaseMapper<SparePart> {
}