package com.eam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eam.entity.Asset;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产 Mapper
 */
@Mapper
public interface AssetMapper extends BaseMapper<Asset> {
}