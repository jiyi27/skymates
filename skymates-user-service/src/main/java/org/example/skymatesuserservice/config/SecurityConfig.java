package org.example.skymatesuserservice.config;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.skymatesuserservice.model.User;
import org.example.skymatesuserservice.repository.UserRepository;
import org.example.skymatesuserservice.security.CustomUserDetails;
import org.example.skymatesuserservice.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.Collections;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("用户名不存在: " + username));

            // 这里返回我们自己的 CustomUserDetails, 它实现了 UserDetails 接口,
            // 与 JwtAuthFilter 中我们创建的 CustomUserDetails 不同, 那里只需要携带用户名和用户ID,
            // 目的是为了 Controller 中的 @AuthenticationPrincipal 注解获取用户信息
            // 此时这里 CustomUserDetails 需要携带 完整的账号密码, 因为返回这个对象是为了给 DaoAuthenticationProvider 验证密码
            return new CustomUserDetails(
                    user.getId(),
                    user.getUsername(),
                    user.getPasswordHash(),
                    Collections.emptyList()
            );
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthFilter jwtAuthenticationFilter) throws Exception {

        // 禁用 CSRF（因为使用的是 JWT 认证）
        http.csrf(AbstractHttpConfigurer::disable);

        // 设置会话管理策略为无状态（STATELESS），适用于 JWT 认证
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 配置授权规则
        http.authorizeHttpRequests(auth -> auth
                // 允许所有人访问登录和注册接口
                .requestMatchers("/api/users/login", "/api/users/register").permitAll()
                // 允许所有人访问错误页面（防止 Spring Security 拦截 500, 400 等错误页面）
                // 如果不配置，因抛出异常而返回错误码如 409, 400 时，Spring Security 会拦截错误页面，
                // 客户端看到的永远是返回 403 状态码
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                // 其他请求都需要身份验证
                .anyRequest().authenticated()
        );

        // 在 UsernamePasswordAuthenticationFilter 之前添加自定义的 JWT 认证过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}

