package com.eam.security;

import com.eam.entity.SysUser;
import com.eam.entity.SysPermission;
import com.eam.repository.SysUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户详情服务实现类 - 用于 Spring Security 身份验证
 * 根据 sys_user 和 sys_permission 表构建用户权限信息
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final SysUserRepository sysUserRepository;

    @Autowired
    public UserDetailsServiceImpl(SysUserRepository sysUserRepository) {
        this.sysUserRepository = sysUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("加载用户信息: {}", username);

        // 查询用户
        SysUser user = sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        // 查询用户权限
        List<String> permissions = getUserPermissions(user.getId());

        return new org.springframework.security.core.userdetails.User(
                username,
                user.getPassword(),
                user.getStatus() != null && user.getStatus() == 1,
                true,
                true,
                true,
                permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }

    /**
     * 获取用户权限列表
     */
    private List<String> getUserPermissions(Long userId) {
        // 这里简化处理，实际项目中应该从 sys_permission 表查询
        // TODO: 实现完整的权限查询逻辑
        return new ArrayList<>();
    }
}