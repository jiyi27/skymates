package org.example.skymatesbackend.service.impl;

import org.example.skymatesbackend.model.CommentLike;
import org.example.skymatesbackend.model.PostLike;
import org.example.skymatesbackend.repository.CommentLikeRepository;
import org.example.skymatesbackend.repository.CommentRepository;
import org.example.skymatesbackend.repository.PostLikeRepository;
import org.example.skymatesbackend.repository.PostRepository;
import org.example.skymatesbackend.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class LikeServiceImpl implements LikeService {
    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final RedisTemplate<String, Long> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public LikeServiceImpl(
            PostLikeRepository postLikeRepository,
            CommentLikeRepository commentLikeRepository,
            PostRepository postRepository,
            CommentRepository commentRepository,
            RedisTemplate<String, Long> redisTemplate,
            KafkaTemplate<String, String> kafkaTemplate) {
        this.postLikeRepository = postLikeRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void likePost(Long postId, Long userId) {
        String likesUsersKey = "post:" + postId + ":likes_users";
        String likesCountKey = "post:" + postId + ":likes_count";
        // 1. 检查是否已点赞
        if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(likesUsersKey, userId))) {
            return; // 已点赞，直接返回
        }

        // 2. Redis 操作：添加用户ID到点赞集合 & 计数+1
        redisTemplate.opsForSet().add(likesUsersKey, userId);
        redisTemplate.opsForValue().increment(likesCountKey, 1);

        // 3. 发送 Kafka 事件 (type=like)
        String event = "like," + postId + "," + userId;
        kafkaTemplate.send("like-topic", event);
    }

    @Override
    public void unlikePost(Long postId, Long userId) {
        String likesUsersKey = "post:" + postId + ":likes_users";
        String likesCountKey = "post:" + postId + ":likes_count";

        // 1. 检查用户是否已点赞
        if (!Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(likesUsersKey, userId))) {
            return; // 未点赞，直接返回
        }

        // 2. 从 Redis 中移除点赞 & 减少计数
        redisTemplate.opsForSet().remove(likesUsersKey, userId);
        Long currentCount = redisTemplate.opsForValue().get(likesCountKey);
        if (currentCount != null && currentCount > 0) {
            redisTemplate.opsForValue().decrement(likesCountKey);
        }

        // 3. 发送 Kafka 事件
        String event = "unlike," + postId + "," + userId;
        kafkaTemplate.send("like-topic", event);
    }

    // Kafka 消费者处理点赞和取消点赞事件
    @KafkaListener(topics = "like-topic", groupId = "like-group")
    public void consumeLikeEvent(String event) {
        String[] parts = event.split(",");
        String type = parts[0];
        Long postId = Long.parseLong(parts[1]);
        Long userId = Long.parseLong(parts[2]);

        if ("like".equals(type)) {
            handleLikeEvent(postId, userId);
        } else if ("unlike".equals(type)) {
            handleUnlikeEvent(postId, userId);
        }
    }

    protected void handleLikeEvent(Long postId, Long userId) {
        // 这里也可以做existsByPostIdAndUserId查询（减少无用插入尝试）
        // 但高并发情况下，还要依赖数据库唯一索引来保证不重复
        if (!postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            try {
                PostLike postLike = new PostLike();
                postLike.setPostId(postId);
                postLike.setUserId(userId);
                postLikeRepository.save(postLike);

                // 更新帖子点赞数 +1
                postRepository.updateLikesCount(postId, 1);
            } catch (DataIntegrityViolationException e) {
                // 唯一性约束冲突和外键约束冲突(postLike插入前会检查post.postId是否存在), 都会抛出 DataIntegrityViolationException
                throw new ResponseStatusException(HttpStatus.CONFLICT, "点赞失败, 已经点赞过该帖子或帖子不存在");
            }
        }
    }

    // 删除数据并不存在“插入唯一键重复”这种问题, 所以不用进行异常捕获
    // 如果 post_like 表里本就没有 (postId, userId) 这条点赞记录，则不会执行删除
    protected void handleUnlikeEvent(Long postId, Long userId) {
        // 如果本地查询到有记录，再尝试删除
        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            postLikeRepository.deleteByPostIdAndUserId(postId, userId);
            // 更新帖子点赞数 -1
            postRepository.updateLikesCount(postId, -1);
        }
    }

    @Override
    public void likeComment(Long commentId, Long userId) {
        try {
            CommentLike commentLike = new CommentLike();
            commentLike.setCommentId(commentId);
            commentLike.setUserId(userId);
            commentLike.setCreatedAt(LocalDateTime.now());
            // 直接插入, 数据库唯一约束防止重复点赞, 外键约束防止帖子不存在, 否则抛出 DataIntegrityViolationException
            commentLikeRepository.save(commentLike);

            commentRepository.updateLikesCount(commentId, 1);
        } catch (DataIntegrityViolationException e) {
            // 唯一性约束冲突和外键约束冲突(commentLike插入前会检查comment.commentId是否存在), 都会抛出 DataIntegrityViolationException
            throw new ResponseStatusException(HttpStatus.CONFLICT, "点赞失败, 已经点赞过该评论或评论不存在");
        }
    }

    @Override
    public void unlikeComment(Long commentId, Long userId) {
        try {
            // 直接删除，返回删除的记录数
            int deletedCount = commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
            if (deletedCount == 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "未点赞过该评论");
            }
            commentRepository.updateLikesCount(commentId, -1);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "取消点赞失败");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getUserLikedPostIds(Long userId) {
        return postLikeRepository.findPostIdsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getUserLikedCommentIds(Long userId) {
        return commentLikeRepository.findCommentIdsByUserId(userId);
    }
}
