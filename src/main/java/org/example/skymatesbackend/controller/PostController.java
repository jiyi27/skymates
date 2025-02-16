package org.example.skymatesbackend.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.skymatesbackend.security.CustomUserDetails;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.example.skymatesbackend.dto.PageDTO;
import org.example.skymatesbackend.dto.PostDTO;
import org.example.skymatesbackend.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDTO> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid PostDTO.CreateRequest request) {
        PostDTO post = postService.createPost(userDetails.getId(), request);
        return ResponseEntity.ok(post);
    }

    @GetMapping
    public ResponseEntity<PageDTO<PostDTO>> getPosts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PageDTO<PostDTO> posts = postService.getPosts(keyword, page, size, userDetails.getId());
        return ResponseEntity.ok(posts);
    }

    /**
     * @PathVariable 必须加上("postId")，否则会报错
     * java.lang.IllegalArgumentException: Name for argument of type [java.lang.Long] not specified,
     * and parameter name information not available via reflection.
     * Ensure that the compiler uses the '-parameters' flag.
     * <a href="https://stackoverflow.com/a/25797744/16317008">...</a>
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPost(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PostDTO post = postService.getPostById(postId, userDetails.getId());
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid PostDTO.UpdateRequest request) {

        PostDTO post = postService.updatePost(postId, userDetails.getId(), request);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        postService.deletePost(postId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PageDTO<PostDTO>> getUserPosts(
            @PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {

        PageDTO<PostDTO> posts = postService.getUserPosts(userId, page, size);
        return ResponseEntity.ok(posts);
    }
}

