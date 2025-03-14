package org.example.skymatesbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private UserDTO author;
    private Integer likesCount;
    private Integer commentsCount;
    private Boolean isLiked;  // 当前用户是否已点赞
    private LocalDateTime createdAt;

    // 用于创建帖子
    @Getter
    @Setter
    public static class CreateRequest {
        @NotBlank(message = "标题不能为空")
        @Size(max = 200, message = "标题最大长度为200")
        private String title;

        @NotBlank(message = "内容不能为空")
        private String content;
    }

    // 用于更新帖子
    @Getter
    @Setter
    public static class UpdateRequest {
        @Size(max = 200, message = "标题最大长度为200")
        private String title;
        private String content;
    }
}
