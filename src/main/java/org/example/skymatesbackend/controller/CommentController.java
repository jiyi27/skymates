package org.example.skymatesbackend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.example.skymatesbackend.dto.CommentDTO;
import org.example.skymatesbackend.dto.PageDTO;
import org.example.skymatesbackend.security.CustomUserDetails;
import org.example.skymatesbackend.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid CommentDTO.CreateRequest request) {

        CommentDTO comment = commentService.createComment(postId, userDetails.getId(), request);
        URI location = URI.create(String.format("/api/posts/%d/comments/%d", postId, comment.getId()));
        return ResponseEntity.created(location).body(comment);
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
            @PathVariable("postId") Long postId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        PageDTO<CommentDTO> comments = commentService.getPostComments(postId, page, size, userDetails.getId());
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
            @SuppressWarnings("unused") @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<CommentDTO> replies = commentService.getCommentReplies(commentId, userDetails.getId());
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
            @SuppressWarnings("unused") @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        commentService.deleteComment(commentId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
