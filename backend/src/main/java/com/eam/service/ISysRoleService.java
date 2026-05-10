package com.eam.service;

import com.eam.entity.SysRole;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 系统角色 Service 接口
 */
public interface ISysRoleService {

    Page<SysRole> page(Long pageNum, Long pageSize, String roleName);

    SysRole add(SysRole role);

    SysRole update(SysRole role);

    boolean delete(Long id);

    List<SysRole> listAll();

    SysRole getById(Long id);

    boolean assignPermissions(Long roleId, List<Long> permIds);
}