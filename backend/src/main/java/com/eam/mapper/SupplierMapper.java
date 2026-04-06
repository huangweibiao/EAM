package com.eam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eam.entity.Supplier;
import org.apache.ibatis.annotations.Mapper;

/**
 * 供应商 Mapper
 */
@Mapper
public interface SupplierMapper extends BaseMapper<Supplier> {
}