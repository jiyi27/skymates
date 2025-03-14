package org.example.skymatesbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CommentDTO {
    private Long id;
    private Long postId;
    private UserDTO author;
    private String content;
    private Integer likesCount;
    private Integer repliesCount;
    private Long parentId;
    private List<CommentDTO> replies;
    private Boolean isLiked;  // 当前用户是否已点赞
    private LocalDateTime createdAt;

    // 用于创建评论
    @Getter
    @Setter
    public static class CreateRequest {
        @NotBlank(message = "评论内容不能为空")
        private String content;

        private Long parentId;  // 回复评论时使用
    }
}
