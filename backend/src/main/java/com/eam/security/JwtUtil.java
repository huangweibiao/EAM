package com.eam.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT 工具类 - 根据 JWT 标准生成和验证 token
 */
@Service
public class JwtUtil {

    @Value("${jwt.secret:EAM-JWT-SECRET-KEY-2026}")
    private String jwtSecret;

    // Token有效期：2小时
    private static final long JWT_EXPIRATION = 1000 * 60 * 60 * 2; // 2小时，单位毫秒

    /**
     * 生成用户 token
     */
    public String generateToken(String username, Map<String, Object> claims) {
        Map<String, Object> userClaims = new HashMap<>();
        userClaims.put("sub", username);
        userClaims.putAll(claims);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .claims(userClaims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从 token 中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证 token 有效性
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 检查 token 是否过期
            Date now = new Date();
            return !claims.getExpiration().before(now);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取 token 中的 Claims
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
