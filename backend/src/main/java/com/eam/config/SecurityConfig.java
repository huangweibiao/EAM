package com.eam.config;

import com.eam.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.servlet.SecurityFilterChain;

/**
 * Spring Security Configuration
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          AuthenticationManager authenticationManager) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationManager = authenticationManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(org.springframework.security.web.SecurityFilterChain http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .disable())
            .authorizeHttpRequests(auth -> auth
                // 登录接口和静态资源公开访问
                .requestMatchers("/api/auth/**", "/static/**", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                // 健康检查接口公开访问
                .requestMatchers("/health").permitAll()
                // WebSocket 端点公开访问
                .requestMatchers("/ws/**").permitAll()
                // 所有其他接口需要认证
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            .httpBasic(Customizer.withDefaults())
            .build();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public org.springframework.security.authentication.AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}