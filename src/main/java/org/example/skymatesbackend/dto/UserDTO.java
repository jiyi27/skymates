package org.example.skymatesbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;

    // 用于创建用户
    @Data
    public static class CreateRequest {
        @NotBlank(message = "用户名不能为空")
        @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
        private String username;

        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        private String email;

        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
        private String password;
    }
}
