package org.example.skymatesbackend.security;

import org.example.skymatesbackend.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 如果有角色/权限，需要在这里转换成 Spring Security 的 GrantedAuthority
        // 示例： return AuthorityUtils.createAuthorityList(user.getRoles().toArray(new String[0]));
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        // 返回数据库里存的加密后密码
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }
}

