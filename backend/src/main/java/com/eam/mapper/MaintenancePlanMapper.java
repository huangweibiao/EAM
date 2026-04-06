package com.eam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eam.entity.MaintenancePlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 维护计划 Mapper
 */
@Mapper
public interface MaintenancePlanMapper extends BaseMapper<MaintenancePlan> {
}