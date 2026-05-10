package com.eam.controller;

import com.eam.common.Result;
import com.eam.dto.LoginResponse;
import com.eam.entity.SysUser;
import com.eam.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication Controller
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ISysUserService sysUserService;

    /**
     * Get current user info
     */
    @GetMapping("/user")
    public Result<LoginResponse.UserInfo> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();

        SysUser user = sysUserService.getByUsername(username);

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getAvatar(),
                user.getDeptId(),
                user.getStatus()
        );

        return Result.success(userInfo);
    }

    /**
     * Check if user is authenticated
     */
    @GetMapping("/check")
    public Result<Boolean> checkAuth(Authentication authentication) {
        return Result.success(authentication != null && authentication.isAuthenticated());
    }

    /**
     * Get user info from OAuth2 principal
     */
    @GetMapping("/oidc-user")
    public Result<OidcUser> getOidcUser(@AuthenticationPrincipal OidcUser oidcUser) {
        return Result.success(oidcUser);
    }
}