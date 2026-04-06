package com.eam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eam.entity.AssetInventory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产盘点 Mapper
 */
@Mapper
public interface AssetInventoryMapper extends BaseMapper<AssetInventory> {
}