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
import org.springframework.http.HttpStatus;
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

    @Autowired
    public LikeServiceImpl(
            PostLikeRepository postLikeRepository,
            CommentLikeRepository commentLikeRepository,
            PostRepository postRepository,
            CommentRepository commentRepository) {
        this.postLikeRepository = postLikeRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public void likePost(Long postId, Long userId) {
        try {
            // 直接插入, 数据库唯一约束防止重复点赞
            PostLike postLike = new PostLike();
            postLike.setPostId(postId);
            postLike.setUserId(userId);
            postLike.setCreatedAt(LocalDateTime.now());
            postLikeRepository.save(postLike);

            // 更新帖子的点赞数, MySQL 默认会给 UPDATE 操作会加上 x锁,
            // 自动便可以防止出现两个事务同时更新同一行数据, 导致数据累加错误情况
            postRepository.updateLikesCount(postId, 1);
        } catch (DataIntegrityViolationException e) {
            // 唯一性约束冲突和外键约束冲突(postLike插入前会检查post.postId是否存在), 都会抛出 DataIntegrityViolationException
            throw new ResponseStatusException(HttpStatus.CONFLICT, "点赞失败, 已经点赞过该帖子或帖子不存在");
        }
    }

    @Override
    @Transactional
    public void unlikePost(Long postId, Long userId) {
        // 直接删除，返回删除的记录数
        int deletedCount = postLikeRepository.deleteByPostIdAndUserId(postId, userId);
        if (deletedCount == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "未点赞过该帖子");
        }
        postRepository.updateLikesCount(postId, -1);
    }

    @Override
    @Transactional
    public void likeComment(Long commentId, Long userId) {
        try {
            // 直接插入, 数据库唯一约束防止重复点赞
            CommentLike commentLike = new CommentLike();
            commentLike.setCommentId(commentId);
            commentLike.setUserId(userId);
            commentLike.setCreatedAt(LocalDateTime.now());
            commentLikeRepository.save(commentLike);

            commentRepository.updateLikesCountWithVersion(commentId, 1);
        } catch (DataIntegrityViolationException e) {
            // 唯一性约束冲突和外键约束冲突(commentLike插入前会检查comment.commentId是否存在), 都会抛出 DataIntegrityViolationException
            throw new ResponseStatusException(HttpStatus.CONFLICT, "点赞失败, 已经点赞过该评论或评论不存在");
        }
    }

    @Override
    @Transactional
    public void unlikeComment(Long commentId, Long userId) {
        // 直接删除，返回删除的记录数
        int deletedCount = commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
        if (deletedCount == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "未点赞过该评论");
        }
        commentRepository.updateLikesCountWithVersion(commentId, -1);
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
