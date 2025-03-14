package org.example.skymatesuserservice.security;

import io.jsonwebtoken.Claims;
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
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    @Autowired
    public JwtAuthFilter(JwtTokenService jwtUtils) {
        this.jwtTokenService = jwtUtils;
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
            if (jwt != null && jwtTokenService.validateToken(jwt)) {
                Claims claims = jwtTokenService.getClaimsFromToken(jwt);
                String username = claims.getSubject();
                Long userId = claims.get("userId", Long.class);

                // 返回我们自己的 CustomUserDetails, 它实现了 UserDetails 接口,
                // 方便 Controller 中的 @AuthenticationPrincipal 注解获取用户信息
                UserDetails userDetails = new CustomUserDetails(
                        userId,
                        username,
                        "",
                        Collections.emptyList()
                );

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
