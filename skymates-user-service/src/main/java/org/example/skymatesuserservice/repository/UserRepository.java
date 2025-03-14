package org.example.skymatesuserservice.repository;

import org.example.skymatesuserservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// 用户 Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 根据用户名查找用户
    Optional<User> findByUsername(String username);

    // 根据邮箱查找用户
    Optional<User> findByEmail(String email);

    // 检查用户名是否已存在
    boolean existsByUsername(String username);

    // 检查邮箱是否已存在
    boolean existsByEmail(String email);
}
