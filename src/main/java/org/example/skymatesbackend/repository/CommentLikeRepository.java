package org.example.skymatesbackend.repository;

import org.example.skymatesbackend.model.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    // 检查用户是否已点赞评论
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);
    // 取消点赞, 这里并没有点赞的方法, 因为点赞是通过CommentLikeRepository.save方法实现的
    // 另外注意, 点赞分为两步, 一步是保存点赞记录, 也就是调用CommentLikeRepository.save方法,
    // 另一步是更新评论的点赞数, 也就是调用CommentRepository.updateLikesCount方法
    int deleteByCommentIdAndUserId(Long commentId, Long userId);
    // 统计评论的点赞数
    long countByCommentId(Long commentId);
    // 获取用户点赞的所有评论ID
    List<Long> findCommentIdsByUserId(Long userId);
}