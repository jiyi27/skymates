package org.example.skymatesbackend.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.example.skymatesbackend.dto.PageDTO;
import org.example.skymatesbackend.dto.PostDTO;
import org.example.skymatesbackend.service.PostService;
import org.example.skymatesbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDTO> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid PostDTO.CreateRequest request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        PostDTO post = postService.createPost(userId, request);
        return ResponseEntity.ok(post);
    }

    @GetMapping
    public ResponseEntity<PageDTO<PostDTO>> getPosts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @AuthenticationPrincipal(errorOnInvalidType = false) UserDetails userDetails) {
        Long currentUserId = userDetails != null ?
                Long.parseLong(userDetails.getUsername()) : null;
        PageDTO<PostDTO> posts = postService.getPosts(keyword, page, size, currentUserId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal(errorOnInvalidType = false) UserDetails userDetails) {
        Long currentUserId = userDetails != null ?
                Long.parseLong(userDetails.getUsername()) : null;
        PostDTO post = postService.getPostById(postId, currentUserId);
        return ResponseEntity.ok(post);
    }

    /**
     * 更新帖子
     * @param postId 帖子ID
     * @param request 更新请求
     * @return 更新后的帖子信息
     */
    @PutMapping("/{postId}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid PostDTO.UpdateRequest request) {

        // 获取当前用户ID
        Long userId = Long.parseLong(userDetails.getUsername());

        // 更新帖子
        PostDTO post = postService.updatePost(postId, userId, request);
        return ResponseEntity.ok(post);
    }

    /**
     * 删除帖子
     * @param postId 帖子ID
     * @return 无内容
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 获取当前用户ID
        Long userId = Long.parseLong(userDetails.getUsername());

        // 删除帖子
        postService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取用户的帖子列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 帖子列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<PageDTO<PostDTO>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {

        // 获取用户帖子列表
        PageDTO<PostDTO> posts = postService.getUserPosts(userId, page, size);
        return ResponseEntity.ok(posts);
    }
}

