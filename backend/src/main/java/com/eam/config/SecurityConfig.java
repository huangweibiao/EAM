package com.eam.config;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Spring Security Configuration
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;

    @Autowired
    public SecurityConfig(OAuth2LoginSuccessHandler oauth2LoginSuccessHandler) {
        this.oauth2LoginSuccessHandler = oauth2LoginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Static resources are publicly accessible
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                // OAuth2 login page is publicly accessible
                .requestMatchers("/login", "/login/**").permitAll()
                // Health check endpoint
                .requestMatchers("/health").permitAll()
                // API endpoints
                .requestMatchers("/api/**").authenticated()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .successHandler(oauth2LoginSuccessHandler)
                .failureUrl("/login?error=true")
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
