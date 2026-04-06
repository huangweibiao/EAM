package com.eam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eam.entity.AssetChangeLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产变动记录 Mapper
 */
@Mapper
public interface AssetChangeLogMapper extends BaseMapper<AssetChangeLog> {
}