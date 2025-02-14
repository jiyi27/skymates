package org.example.skymatesbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.skymatesbackend.dto.UserDTO;
import org.example.skymatesbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody @Valid UserDTO.CreateRequest request) {
        UserDTO user = userService.createUser(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO.Response> login(@RequestBody @Valid UserDTO.LoginRequest request) {
        UserDTO.Response user = userService.login(request);
        return ResponseEntity.ok(user);
    }

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 存在状态
     */
    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.isUsernameExists(username);
        return ResponseEntity.ok(exists);
    }

    /**
     * 检查邮箱是否已存在
     * @param email 邮箱
     * @return 存在状态
     */
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.isEmailExists(email);
        return ResponseEntity.ok(exists);
    }
}
