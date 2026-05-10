package com.eam.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth2 Login Success Handler
 */
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        try {
            // Get OAuth2 user info
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            String provider = authentication.getAuthorities().stream()
                    .filter(auth -> auth.getAuthority().startsWith("OAUTH2_"))
                    .map(auth -> auth.getAuthority().substring(7))
                    .findFirst()
                    .orElse("oauth2-server");

            log.info("OAuth2 login success - Provider: {}, Username: {}",
                    provider, oidcUser.getPreferredUsername());

            // TODO: Implement proper user creation/update logic with SysUser and SysUserService
            // This is simplified for compilation - needs proper implementation
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"code\":200,\"message\":\"OAuth2 login successful (simplified implementation)\",\"data\":{\"username\":\"" +
                            oidcUser.getPreferredUsername() + "\"}}"
            );
        } catch (Exception e) {
            log.error("OAuth2 login failed", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":500,\"message\":\"Login failed\"}");
        }
    }
}