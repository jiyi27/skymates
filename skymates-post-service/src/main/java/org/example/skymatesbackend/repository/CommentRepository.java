package org.example.skymatesbackend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.example.skymatesbackend.model.Comment;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 查找帖子的所有一级评论（没有父评论的评论）
    // 直接使用 Pageable 传递 Sort 参数, 而不是硬编码 ORDER BY, 所以 @Query 注解中不需要再指定 ORDER BY
    @Query("SELECT c FROM Comment c WHERE c.postId = :postId AND c.parentId IS NULL AND c.status = 1")
    Page<Comment> findTopLevelComments(@Param("postId") Long postId, Pageable pageable);

    // 查找评论的所有回复
    @Query("SELECT c FROM Comment c WHERE c.parentId = :parentId AND c.status = 1")
    List<Comment> findReplies(@Param("parentId") Long parentId);

    // 查找用户的所有评论
    Page<Comment> findByUserId(@Param("userId") Long userId, Pageable pageable);

    // MySQL 默认会给 UPDATE, Insert, Delete 操作会加上 x锁,
    // 自动便可以防止出现两个事务同时更新同一行数据, 导致数据累加错误情况, 所以没必要使用悲观锁或乐观锁
    @Modifying
    @Query("UPDATE Comment c SET c.likesCount = c.likesCount + :delta WHERE c.id = :commentId")
    void updateLikesCount(
            @Param("commentId") Long commentId,
            @Param("delta") int delta);

    // 更新评论回复数
    @Modifying
    @Query("UPDATE Comment c SET c.repliesCount = c.repliesCount + :delta WHERE c.id = :commentId")
    int updateRepliesCount(
            @Param("commentId") Long commentId,
            @Param("delta") int delta);

    // 软删除评论, 只删除 status = 1 的评论
    @Modifying
    @Query("UPDATE Comment c SET c.status = 2 WHERE c.id = :commentId AND c.userId = :userId AND c.status = 1")
    int softDeleteComment(@Param("commentId") Long commentId, @Param("userId") Long userId);

    // 检查评论是否属于指定帖子
    boolean existsByIdAndPostId(Long commentId, Long postId);
}

