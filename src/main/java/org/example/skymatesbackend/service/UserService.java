package org.example.skymatesbackend.service;

import org.example.skymatesbackend.dto.UserDTO;

public interface UserService {
    // 创建新用户
    UserDTO createUser(UserDTO.CreateRequest request);

    // 用户登录
    UserDTO.Response login(UserDTO.LoginRequest request);

    // 根据ID获取用户
    UserDTO getUserById(Long userId);

    // 根据用户名获取用户
    UserDTO getUserByUsername(String username);

    // 检查用户名或邮箱是否已存在
    boolean isUsernameExists(String username);
    boolean isEmailExists(String email);
}

