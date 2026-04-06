package com.eam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eam.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统菜单/权限 Mapper
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
}