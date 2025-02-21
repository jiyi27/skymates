package org.example.skymatesbackend.repository;

import org.example.skymatesbackend.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 查找用户的所有帖子, 排序可以通过 Pageable 参数传递, status = 1 表示正常状态的帖子
    Page<Post> findByUserIdAndStatus(Long userId, int status, Pageable pageable);

    // 查找所有正常状态的帖子（按创建时间倒序）
    @Query("SELECT p FROM Post p WHERE p.status = 1 ORDER BY p.createdAt DESC")
    Page<Post> findAllActivePosts(Pageable pageable);

    // 根据标题或内容搜索帖子
    @Query("SELECT p FROM Post p WHERE p.status = 1 AND " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> searchPosts(String keyword, Pageable pageable);

    // MySQL 默认会给 UPDATE, Insert, Delete 操作会加上 x锁,
    // 自动便可以防止出现两个事务同时更新同一行数据, 导致数据累加错误情况, 所以没必要使用悲观锁或乐观锁
    @Modifying
    @Query("UPDATE Post p SET p.likesCount = p.likesCount + :delta WHERE p.id = :postId")
    void updateLikesCount(@Param("postId") Long postId, @Param("delta") int delta);

    @Modifying
    @Query("UPDATE Post p SET p.commentsCount = p.commentsCount + :delta WHERE p.id = :postId")
    void updateCommentsCount(
            @Param("postId") Long postId,
            @Param("delta") int delta);

    // 软删除帖子
    @Modifying
    @Query("UPDATE Post p SET p.status = 2 WHERE p.id = :postId AND p.userId = :userId")
    int softDeletePost(@Param("postId") Long postId, @Param("userId") Long userId);
}

