package com.eam.controller;

import com.eam.dto.LoginResponse;
import com.eam.entity.SysUser;
import com.eam.service.ISysUserService;
import com.eam.service.ISysRoleService;
import com.eam.security.JwtAuthenticationFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Authentication Controller
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ISysUserService sysUserService;
    private final ISysRoleService sysRoleService;

    @Autowired
    public AuthController(ISysUserService sysUserService, ISysRoleService sysRoleService) {
        this.sysUserService = sysUserService;
        this.sysRoleService = sysRoleService;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        // 调用 JWT 认证
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> result = new HashMap<>();
        result.put("token", jwtAuthenticationFilter.generateToken(authenticate));

        return result;
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/user")
    public Map<String, Object> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();

        SysUser user = sysUserService.getByUsername(username);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("email", user.getEmail());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("deptId", user.getDeptId());
        userInfo.put("status", user.getStatus());

        Map<String, Object> result = new HashMap<>();
        result.put("userInfo", userInfo);

        return result;
    }

    /**
     * 用户退出
     */
    @PostMapping("/logout")
    public Map<String, Object> logout(Authentication authentication, HttpServletResponse response) {
        SecurityContextHolder.clearContext();

        // 清除 Cookie
        Cookie cookie = new Cookie("token", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "退出成功");

        return result;
    }

    /**
     * 检查用户是否认证
     */
    @GetMapping("/check")
    public Map<String, Object> checkAuth(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        result.put("authenticated", authentication != null && authentication.isAuthenticated());

        return result;
    }

    /**
     * 刷新 Token
     */
    @GetMapping("/refresh")
    public Map<String, Object> refreshToken(Authentication authentication) {
        String token = jwtAuthenticationFilter.generateToken(authentication);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);

        return result;
    }
}