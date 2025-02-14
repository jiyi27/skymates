package org.example.skymatesbackend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.skymatesbackend.model.User;
import org.example.skymatesbackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 通过 UserRepository 从数据库查询用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户名不存在: " + username));

        log.info("用户信息: {}", user);

        // 这里需要把自己的 User 转换成 Spring Security 提供的 UserDetails 对象
        // 例如使用 org.springframework.security.core.userdetails.User
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                // 这里可以把用户权限/角色传进去，比如从数据库查到的 roles 转为 SimpleGrantedAuthority
                Collections.emptyList()
        );
    }
}

