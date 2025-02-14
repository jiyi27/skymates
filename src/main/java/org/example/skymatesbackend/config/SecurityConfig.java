package org.example.skymatesbackend.config;

import lombok.RequiredArgsConstructor;
import org.example.skymatesbackend.security.CustomUserDetailsService;
import org.example.skymatesbackend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    // private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * 配置密码加密方式
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/home").permitAll()
                .anyRequest().authenticated()
        );

        http.authenticationProvider(authenticationProvider()).formLogin(Customizer.withDefaults());

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .expiredUrl("/login?expired")
        );

        return http.build();
    }

//    /**
//     * 核心安全过滤链配置
//     */
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        // 开启csrf可根据需求，这里示例关闭以方便测试API
//        http.csrf(AbstractHttpConfigurer::disable);
//
//        // 对需要忽略的端点放行，比如注册、登录
//        http.authorizeHttpRequests(auth -> auth
//                .requestMatchers("/api/users/register", "/api/users/login").permitAll()
//                .anyRequest().authenticated()
//        );
//
//        // 设置 Session 策略为无状态（JWT通常是无状态的）
//        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        // 重要: 将自定义的 JWT 过滤器, 加入到 Spring Security 的过滤器链中
//        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//
//        // 其他默认配置
//        // http.authenticationProvider(authenticationProvider()).formLogin(Customizer.withDefaults());
//
//        return http.build();
//    }
}