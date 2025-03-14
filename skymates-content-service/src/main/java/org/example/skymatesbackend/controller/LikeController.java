package org.example.skymatesbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.skymatesbackend.security.CustomUserDetails;
import org.example.skymatesbackend.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        likeService.likePost(postId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 取消帖子点赞
     * @param postId 帖子ID
     * @return 无内容
     */
    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<Void> unlikePost(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        likeService.unlikePost(postId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 点赞评论
     * @param commentId 评论ID
     * @return 无内容
     */
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> likeComment(
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        likeService.likeComment(commentId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 取消评论点赞
     * @param commentId 评论ID
     * @return 无内容
     */
    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> unlikeComment(
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        likeService.unlikeComment(commentId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取用户点赞的帖子ID列表
     * @return ID列表
     */
    @GetMapping("/users/me/liked-posts")
    public ResponseEntity<List<Long>> getUserLikedPostIds(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Long> postIds = likeService.getUserLikedPostIds(userDetails.getId());
        return ResponseEntity.ok(postIds);
    }

    /**
     * 获取用户点赞的评论ID列表
     * @return ID列表
     */
    @GetMapping("/users/me/liked-comments")
    public ResponseEntity<List<Long>> getUserLikedCommentIds(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Long> commentIds = likeService.getUserLikedCommentIds(userDetails.getId());
        return ResponseEntity.ok(commentIds);
    }
}
