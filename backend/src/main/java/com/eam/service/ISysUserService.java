package com.eam.service;

import com.eam.entity.SysUser;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 系统用户 Service 接口
 */
public interface ISysUserService {

    Page<SysUser> page(Long pageNum, Long pageSize, String username, Integer status);

    SysUser add(SysUser user);

    SysUser update(SysUser user);

    boolean delete(Long id);

    boolean resetPassword(Long id);

    SysUser getById(Long id);

    List<SysUser> list();

    SysUser getByUsername(String username);

    org.springframework.security.core.Authentication authenticate(org.springframework.security.core.Authentication authentication);
}