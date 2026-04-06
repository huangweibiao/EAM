package com.eam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eam.entity.MaintenanceRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 维护记录 Mapper
 */
@Mapper
public interface MaintenanceRecordMapper extends BaseMapper<MaintenanceRecord> {
}