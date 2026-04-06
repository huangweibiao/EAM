package com.eam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eam.entity.SysRole;

import java.util.List;

/**
 * 系统角色 Service 接口
 */
public interface ISysRoleService extends IService<SysRole> {

    IPage<SysRole> page(Long pageNum, Long pageSize, String roleName);

    SysRole add(SysRole role);

    SysRole update(SysRole role);

    boolean delete(Long id);

    List<SysRole> listAll();

    boolean assignPermissions(Long roleId, List<Long> permIds);
}