package com.eam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eam.entity.SysUser;

/**
 * 系统用户 Service 接口
 */
public interface SysUserService extends IService<SysUser> {

    IPage<SysUser> page(Long pageNum, Long pageSize, String username, Integer status);

    SysUser add(SysUser user);

    SysUser update(SysUser user);

    boolean delete(Long id);

    boolean resetPassword(Long id);

    SysUser getByUsername(String username);
}