package org.example.skymatesbackend.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * 自定义 JWT 认证过滤器
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtUtils;

    @Autowired
    public JwtAuthFilter(JwtTokenService jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 从请求头中获取 Authorization
            String jwt = parseJwt(request);

            // 验证 token 是否有效
            if (jwt != null && jwtUtils.validateToken(jwt)) {
                // 从 token 获取用户名
                String username = jwtUtils.getUsernameFromToken(jwt);

                // 这里可以进一步查询数据库，或用一个 UserDetailsService
                // 简化起见，直接创建一个 UserDetails，设置已认证
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(username)
                        .password("") // 这里只是示例
                        .authorities("USER") // 或者根据实际角色设置
                        .build();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 将认证信息放到安全上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            logger.error("JWT Token is expired:" + e.getMessage());
        } catch (Exception e) {
            logger.error("Cannot set user authentication:" + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中获取 JWT
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}