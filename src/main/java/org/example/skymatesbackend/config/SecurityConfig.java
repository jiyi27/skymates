package org.example.skymatesbackend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.skymatesbackend.model.User;
import org.example.skymatesbackend.repository.UserRepository;
import org.example.skymatesbackend.security.CustomUserDetails;
import org.example.skymatesbackend.security.JwtAuthFilter;
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

        http.csrf(AbstractHttpConfigurer::disable);

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/login", "/api/users/register").permitAll()
                .anyRequest().authenticated()
        );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
