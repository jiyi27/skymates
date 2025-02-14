package org.example.skymatesbackend.service.impl;

import org.example.skymatesbackend.dto.UserDTO;
import org.example.skymatesbackend.security.JwtUtils;
import org.springframework.transaction.annotation.Transactional;
import org.example.skymatesbackend.exception.BusinessException;
import org.example.skymatesbackend.model.User;
import org.example.skymatesbackend.repository.UserRepository;
import org.example.skymatesbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;


@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    /**
     * 注册：检查用户名是否存在 -> 加密密码 -> 保存到数据库 -> 返回 DTO
     */
    @Override
    public UserDTO createUser(UserDTO.CreateRequest request) {
        // 检查用户名和邮箱是否已存在
        if (isUsernameExists(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        if (isEmailExists(request.getEmail())) {
            throw new BusinessException("邮箱已存在");
        }

        // 创建用户实体
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 保存用户
        user = userRepository.save(user);

        return convertToDTO(user);
    }

    @Override
    public UserDTO.Response login(UserDTO.LoginRequest request) {
        // 根据用户名查询用户
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtUtils.generateToken(user.getUsername());

        return UserDTO.Response.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return convertToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return convertToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
