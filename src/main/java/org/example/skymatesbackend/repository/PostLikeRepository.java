package org.example.skymatesbackend.repository;

import org.example.skymatesbackend.model.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    // 是否已点赞帖子
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    // 取消点赞, 这里并没有点赞的方法, 因为点赞是通过PostLikeRepository.save方法实现的
    // 另外注意, 点赞分为两步, 一步是保存点赞记录, 也就是调用PostLikeRepository.save方法,
    // 另一步是更新帖子的点赞数, 也就是调用PostRepository.updateLikesCount方法
    int deleteByPostIdAndUserId(Long postId, Long userId);
    // 统计帖子的点赞数
    long countByPostId(Long postId);
    // 获取用户点赞的所有帖子ID
    @Query("SELECT p.postId FROM PostLike p WHERE p.userId = :userId")
    List<Long> findPostIdsByUserId(@Param("userId") Long userId);
}