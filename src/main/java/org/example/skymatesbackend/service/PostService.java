package org.example.skymatesbackend.service;

import org.example.skymatesbackend.dto.PageDTO;
import org.example.skymatesbackend.dto.PostDTO;

public interface PostService {
    // 创建帖子
    PostDTO createPost(Long userId, PostDTO.CreateRequest request);

    // 获取帖子详情
    PostDTO getPostById(Long postId, Long currentUserId);

    // 更新帖子
    PostDTO updatePost(Long postId, Long userId, PostDTO.UpdateRequest request);

    // 删除帖子
    void deletePost(Long postId, Long userId);

    // 获取用户的帖子列表
    PageDTO<PostDTO> getUserPosts(Long userId, int page, int size);

    // 获取帖子列表（首页、搜索等）
    PageDTO<PostDTO> getPosts(String keyword, int page, int size, Long currentUserId);
}
