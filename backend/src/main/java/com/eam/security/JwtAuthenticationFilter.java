package com.eam.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 * 从请求中提取 JWT Token，验证其有效性，并设置用户认证信息
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final String jwtSecret;

    public JwtAuthenticationFilter() {
        this.jwtSecret = "EAM-JWT-SECRET-KEY-2026";
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 从请求中提取 JWT Token
        String token = extractToken(request);
        if (token != null && validateToken(token)) {
            // 验证 Token 有效性
            UsernamePasswordAuthenticationToken authentication = getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中提取 JWT Token
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 验证 JWT Token 有效性
     */
    private boolean validateToken(String token) {
        try {
            // 这里简化验证，实际项目中应该解析和验证 JWT
            return token != null && token.length() > 20;
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return false;
        }
    }

    /**
     * 根据 Token 生成用户认证信息
     */
    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        // 这里简化处理，实际项目中应该从 JWT 中解析用户信息
        return new UsernamePasswordAuthenticationToken(token, "user", java.util.Collections.emptyList());
    }

    /**
     * 清除用户认证
     */
    public void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    /**
     * 生成 JWT Token（简化版本）
     */
    public String generateToken(org.springframework.security.core.Authentication authentication) {
        // 这里简化处理，实际项目中应该使用 JWT 库生成 Token
        return "JWT-TOKEN-" + System.currentTimeMillis();
    }
}