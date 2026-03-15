package com.eam.security;

import com.eam.dto.LoginResponse;
import com.eam.entity.User;
import com.eam.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * OAuth2 Login Success Handler
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

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

            // Create or update user
            User user = userService.createOrUpdateUserFromOidcUser(oidcUser, provider);

            // Build login response
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getNickname(),
                    user.getEmail(),
                    user.getAvatar(),
                    user.getDeptId(),
                    user.getStatus()
            );

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccessToken("OAuth2-AUTH-" + user.getId());
            loginResponse.setUserInfo(userInfo);

            // Return JSON response
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"code\":200,\"message\":\"Login successful\",\"data\":{" +
                            "\"accessToken\":\"" + loginResponse.getAccessToken() + "\"," +
                            "\"tokenType\":\"Bearer\"," +
                            "\"userInfo\":{" +
                            "\"id\":" + userInfo.getId() + "," +
                            "\"username\":\"" + escapeJson(userInfo.getUsername()) + "\"," +
                            "\"nickname\":\"" + escapeJson(userInfo.getNickname()) + "\"," +
                            "\"email\":\"" + escapeJson(userInfo.getEmail()) + "\"," +
                            "\"avatar\":\"" + escapeJson(userInfo.getAvatar()) + "\"," +
                            "\"deptId\":" + (userInfo.getDeptId() != null ? userInfo.getDeptId() : "null") + "," +
                            "\"status\":" + userInfo.getStatus() +
                            "}}}"
            );
        } catch (Exception e) {
            log.error("OAuth2 login failed", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":500,\"message\":\"Login failed: " +
                    escapeJson(e.getMessage()) + "\"}");
        }
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
