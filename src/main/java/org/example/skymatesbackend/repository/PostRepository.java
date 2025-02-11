package org.example.skymatesbackend.repository;

import org.example.skymatesbackend.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 查找用户的所有帖子
    @Query("SELECT p FROM Post p WHERE p.userId = :userId AND p.status = 1")
    Page<Post> findByUserId(Long userId, Pageable pageable);

    // 查找所有正常状态的帖子（按创建时间倒序）
    @Query("SELECT p FROM Post p WHERE p.status = 1 ORDER BY p.createdAt DESC")
    Page<Post> findAllActivePosts(Pageable pageable);

    // 根据标题或内容搜索帖子
    @Query("SELECT p FROM Post p WHERE p.status = 1 AND " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> searchPosts(String keyword, Pageable pageable);

    /**
     * 带乐观锁的自定义更新帖子点赞数
     */
    @Modifying
    @Query("UPDATE Post p " +
            "SET p.likesCount = p.likesCount + :delta, " +
            "    p.version = p.version + 1 " +
            "WHERE p.id = :postId AND p.version = :version")
    int updateLikesCountWithVersion(
            @Param("postId") Long postId,
            @Param("version") int version,
            @Param("delta") int delta);

    // 更新帖子评论数
    @Modifying
    @Query("UPDATE Post p SET p.commentsCount = p.commentsCount + :delta WHERE p.id = :postId")
    void updateCommentsCount(@Param("postId") Long postId, @Param("delta") int delta);

    // 软删除帖子
    @Modifying
    @Query("UPDATE Post p SET p.status = 2 WHERE p.id = :postId AND p.userId = :userId")
    int softDeletePost(@Param("postId") Long postId, @Param("userId") Long userId);
}

