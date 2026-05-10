package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.SysDepartment;
import com.eam.repository.SysDepartmentRepository;
import com.eam.service.ISysDepartmentService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统部门 Service 实现类
 */
@Service
public class SysDepartmentServiceImpl implements ISysDepartmentService {

    private final SysDepartmentRepository departmentRepository;

    public SysDepartmentServiceImpl(SysDepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<SysDepartment> tree() {
        // 获取所有部门
        List<SysDepartment> allDepts = departmentRepository.findAll();
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
            Specification<SysDepartment> spec = (root, query, cb) ->
                    cb.equal(root.get("deptCode"), dept.getDeptCode());
            long count = departmentRepository.count(spec);
            if (count > 0) {
                throw new BusinessException("部门编码已存在");
            }
        }
        return departmentRepository.save(dept);
    }

    @Override
    public SysDepartment update(SysDepartment dept) {
        if (dept.getId() == null) {
            throw new BusinessException("部门ID不能为空");
        }
        return departmentRepository.save(dept);
    }

    @Override
    public boolean delete(Long id) {
        // 检查是否有子部门
        Specification<SysDepartment> spec = (root, query, cb) ->
                cb.equal(root.get("parentId"), id);
        long count = departmentRepository.count(spec);
        if (count > 0) {
            throw new BusinessException("请先删除子部门");
        }
        departmentRepository.deleteById(id);
        return true;
    }

    @Override
    public List<SysDepartment> listChildren(Long parentId) {
        return departmentRepository.findByParentIdOrderBySortOrderAsc(parentId);
    }

    @Override
    public SysDepartment getById(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }

    @Override
    public List<SysDepartment> list() {
        return departmentRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder"));
    }
}