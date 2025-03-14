package org.example.skymatesbackend.service;

import java.util.List;

public interface LikeService {
    // 点赞帖子
    void likePost(Long postId, Long userId);

    // 取消帖子点赞
    void unlikePost(Long postId, Long userId);

    // 点赞评论
    void likeComment(Long commentId, Long userId);

    // 取消评论点赞
    void unlikeComment(Long commentId, Long userId);

    // 获取用户点赞的帖子ID列表
    List<Long> getUserLikedPostIds(Long userId);

    // 获取用户点赞的评论ID列表
    List<Long> getUserLikedCommentIds(Long userId);
}
