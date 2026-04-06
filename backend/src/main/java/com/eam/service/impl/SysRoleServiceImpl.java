package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eam.common.BusinessException;
import com.eam.entity.SysRole;
import com.eam.mapper.SysRoleMapper;
import com.eam.service.ISysRoleService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 系统角色 Service 实现类
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Override
    public IPage<SysRole> page(Long pageNum, Long pageSize, String roleName) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(roleName)) {
            wrapper.like(SysRole::getRoleName, roleName);
        }
        wrapper.orderByDesc(SysRole::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public SysRole add(SysRole role) {
        // 检查角色key是否存在
        if (StringUtils.hasText(role.getRoleKey())) {
            Long count = this.count(new LambdaQueryWrapper<SysRole>()
                    .eq(SysRole::getRoleKey, role.getRoleKey()));
            if (count > 0) {
                throw new BusinessException("角色编码已存在");
            }
        }
        this.save(role);
        return role;
    }

    @Override
    public SysRole update(SysRole role) {
        if (role.getId() == null) {
            throw new BusinessException("角色ID不能为空");
        }
        this.updateById(role);
        return role;
    }

    @Override
    public boolean delete(Long id) {
        // TODO: 检查是否有用户关联该角色
        return this.removeById(id);
    }

    @Override
    public List<SysRole> listAll() {
        return this.list(new LambdaQueryWrapper<SysRole>()
                .orderByAsc(SysRole::getRoleSort));
    }

    @Override
    public boolean assignPermissions(Long roleId, List<Long> permIds) {
        // TODO: 实现角色权限分配逻辑
        return true;
    }
}