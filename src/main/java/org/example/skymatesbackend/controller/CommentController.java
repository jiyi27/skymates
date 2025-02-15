package org.example.skymatesbackend.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.example.skymatesbackend.dto.CommentDTO;
import org.example.skymatesbackend.dto.PageDTO;
import org.example.skymatesbackend.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    /**
     * 创建评论
     * @param postId 帖子ID
     * @param request 评论创建请求
     * @return 创建的评论信息
     */
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CommentDTO.CreateRequest request) {

        // 获取当前用户ID
        Long userId = Long.parseLong(userDetails.getUsername());

        // 创建评论
        CommentDTO comment = commentService.createComment(postId, userId, request);
        return ResponseEntity.ok(comment);
    }

    /**
     * 获取帖子的评论列表
     * @param postId 帖子ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论列表
     */
    @GetMapping
    public ResponseEntity<PageDTO<CommentDTO>> getPostComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @AuthenticationPrincipal() UserDetails userDetails) {

        // 获取当前用户ID（如果已登录）
        Long currentUserId = userDetails != null ?
                Long.parseLong(userDetails.getUsername()) : null;

        // 获取评论列表
        PageDTO<CommentDTO> comments = commentService.getPostComments(postId, page, size, currentUserId);
        return ResponseEntity.ok(comments);
    }

    /**
     * 获取评论的回复列表
     * @param postId 帖子ID
     * @param commentId 评论ID
     * @return 回复列表
     */
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentDTO>> getCommentReplies(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal(errorOnInvalidType = false) UserDetails userDetails) {

        // 获取当前用户ID（如果已登录）
        Long currentUserId = userDetails != null ?
                Long.parseLong(userDetails.getUsername()) : null;

        // 获取回复列表
        List<CommentDTO> replies = commentService.getCommentReplies(commentId, currentUserId);
        return ResponseEntity.ok(replies);
    }

    /**
     * 删除评论
     * @param postId 帖子ID
     * @param commentId 评论ID
     * @return 无内容
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 获取当前用户ID
        Long userId = Long.parseLong(userDetails.getUsername());

        // 删除评论
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
