package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.SysRole;
import com.eam.repository.SysRoleRepository;
import com.eam.service.ISysRoleService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统角色 Service 实现类
 */
@Service
public class SysRoleServiceImpl implements ISysRoleService {

    private final SysRoleRepository roleRepository;

    public SysRoleServiceImpl(SysRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Page<SysRole> page(Long pageNum, Long pageSize, String roleName) {
        // JPA 分页从 0 开始，MyBatis-Plus 从 1 开始，需要转换
        Pageable pageable = PageRequest.of(pageNum.intValue() - 1, pageSize.intValue(),
                Sort.by(Sort.Direction.DESC, "createTime"));

        Specification<SysRole> spec = (root, query, cb) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(roleName)) {
                predicates.add(cb.like(root.get("roleName"), "%" + roleName + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return roleRepository.findAll(spec, pageable);
    }

    @Override
    public SysRole add(SysRole role) {
        // 检查角色key是否存在
        if (StringUtils.hasText(role.getRoleKey())) {
            Specification<SysRole> spec = (root, query, cb) ->
                    cb.equal(root.get("roleKey"), role.getRoleKey());
            long count = roleRepository.count(spec);
            if (count > 0) {
                throw new BusinessException("角色编码已存在");
            }
        }
        return roleRepository.save(role);
    }

    @Override
    public SysRole update(SysRole role) {
        if (role.getId() == null) {
            throw new BusinessException("角色ID不能为空");
        }
        return roleRepository.save(role);
    }

    @Override
    public boolean delete(Long id) {
        // TODO: 检查是否有用户关联该角色
        roleRepository.deleteById(id);
        return true;
    }

    @Override
    public List<SysRole> listAll() {
        return roleRepository.findAll(Sort.by(Sort.Direction.ASC, "roleSort"));
    }

    @Override
    public SysRole getById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public boolean assignPermissions(Long roleId, List<Long> permIds) {
        // TODO: 实现角色权限分配逻辑
        return true;
    }
}