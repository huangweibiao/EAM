package com.eam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eam.entity.WorkOrderPart;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工单-备件关联 Mapper
 */
@Mapper
public interface WorkOrderPartMapper extends BaseMapper<WorkOrderPart> {
}