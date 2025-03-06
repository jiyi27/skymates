package org.example.skymatesbackend.service;

import org.example.skymatesbackend.dto.CommentDTO;
import org.example.skymatesbackend.dto.PageDTO;

import java.util.List;

public interface CommentService {
    // 创建评论
    CommentDTO createComment(Long postId, Long userId, CommentDTO.CreateRequest request);

    // 获取帖子的评论列表
    PageDTO<CommentDTO> getPostComments(Long postId, int page, int size, Long currentUserId);

    // 获取评论的回复列表
    List<CommentDTO> getCommentReplies(Long commentId, Long currentUserId);

    // 删除评论
    void deleteComment(Long commentId, Long userId);
}
