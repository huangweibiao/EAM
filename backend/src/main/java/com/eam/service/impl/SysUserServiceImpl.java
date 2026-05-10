package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.SysUser;
import com.eam.repository.SysUserRepository;
import com.eam.service.ISysUserService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统用户 Service 实现类
 */
@Service
public class SysUserServiceImpl implements ISysUserService {

    private final SysUserRepository userRepository;

    public SysUserServiceImpl(SysUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<SysUser> page(Long pageNum, Long pageSize, String username, Integer status) {
        // JPA 分页从 0 开始，MyBatis-Plus 从 1 开始，需要转换
        Pageable pageable = PageRequest.of(pageNum.intValue() - 1, pageSize.intValue(),
                Sort.by(Sort.Direction.DESC, "createTime"));

        Specification<SysUser> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(username)) {
                predicates.add(cb.like(root.get("username"), "%" + username + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return userRepository.findAll(spec, pageable);
    }

    @Override
    public SysUser add(SysUser user) {
        // 检查用户名是否存在
        SysUser existUser = getByUsername(user.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public SysUser update(SysUser user) {
        if (user.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        user.setUpdateTime(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public boolean delete(Long id) {
        if (id == 1) {
            throw new BusinessException("不能删除超级管理员");
        }
        userRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean resetPassword(Long id) {
        SysUser user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword("123456"); // 默认密码
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        return true;
    }

    @Override
    public SysUser getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<SysUser> list() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
    }

    @Override
    public SysUser getByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
