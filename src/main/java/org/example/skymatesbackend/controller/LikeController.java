package org.example.skymatesbackend.controller;


import lombok.RequiredArgsConstructor;
import org.example.skymatesbackend.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    /**
     * 点赞帖子
     * @param postId 帖子ID
     * @return 无内容
     */
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Void> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        likeService.likePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 取消帖子点赞
     * @param postId 帖子ID
     * @return 无内容
     */
    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        likeService.unlikePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 点赞评论
     * @param commentId 评论ID
     * @return 无内容
     */
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> likeComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        likeService.likeComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 取消评论点赞
     * @param commentId 评论ID
     * @return 无内容
     */
    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> unlikeComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        likeService.unlikeComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取用户点赞的帖子ID列表
     * @return ID列表
     */
    @GetMapping("/users/me/liked-posts")
    public ResponseEntity<List<Long>> getUserLikedPostIds(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<Long> postIds = likeService.getUserLikedPostIds(userId);
        return ResponseEntity.ok(postIds);
    }

    /**
     * 获取用户点赞的评论ID列表
     * @return ID列表
     */
    @GetMapping("/users/me/liked-comments")
    public ResponseEntity<List<Long>> getUserLikedCommentIds(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<Long> commentIds = likeService.getUserLikedCommentIds(userId);
        return ResponseEntity.ok(commentIds);
    }
}
