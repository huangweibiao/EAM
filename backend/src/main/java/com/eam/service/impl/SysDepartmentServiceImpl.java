package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eam.common.BusinessException;
import com.eam.entity.SysDepartment;
import com.eam.mapper.SysDepartmentMapper;
import com.eam.service.ISysDepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统部门 Service 实现类
 */
@Service
public class SysDepartmentServiceImpl extends ServiceImpl<SysDepartmentMapper, SysDepartment> implements ISysDepartmentService {

    @Override
    public List<SysDepartment> tree() {
        // 获取所有部门
        List<SysDepartment> allDepts = this.list();
        // 构建树形结构
        return buildTree(allDepts, 0L);
    }

    private List<SysDepartment> buildTree(List<SysDepartment> allDepts, Long parentId) {
        return allDepts.stream()
                .filter(dept -> dept.getParentId().equals(parentId))
                .peek(dept -> dept.setChildren(buildTree(allDepts, dept.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public SysDepartment add(SysDepartment dept) {
        // 检查编码是否存在
        if (StringUtils.hasText(dept.getDeptCode())) {
            Long count = this.count(new LambdaQueryWrapper<SysDepartment>()
                    .eq(SysDepartment::getDeptCode, dept.getDeptCode()));
            if (count > 0) {
                throw new BusinessException("部门编码已存在");
            }
        }
        this.save(dept);
        return dept;
    }

    @Override
    public SysDepartment update(SysDepartment dept) {
        if (dept.getId() == null) {
            throw new BusinessException("部门ID不能为空");
        }
        this.updateById(dept);
        return dept;
    }

    @Override
    public boolean delete(Long id) {
        // 检查是否有子部门
        Long count = this.count(new LambdaQueryWrapper<SysDepartment>()
                .eq(SysDepartment::getParentId, id));
        if (count > 0) {
            throw new BusinessException("请先删除子部门");
        }
        return this.removeById(id);
    }

    @Override
    public List<SysDepartment> listChildren(Long parentId) {
        return this.list(new LambdaQueryWrapper<SysDepartment>()
                .eq(SysDepartment::getParentId, parentId)
                .orderByAsc(SysDepartment::getSortOrder));
    }
}