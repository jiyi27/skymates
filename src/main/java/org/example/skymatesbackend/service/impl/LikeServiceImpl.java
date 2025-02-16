package org.example.skymatesbackend.service.impl;

import org.example.skymatesbackend.model.Comment;
import org.example.skymatesbackend.model.CommentLike;
import org.example.skymatesbackend.model.Post;
import org.example.skymatesbackend.model.PostLike;
import org.example.skymatesbackend.repository.CommentLikeRepository;
import org.example.skymatesbackend.repository.CommentRepository;
import org.example.skymatesbackend.repository.PostLikeRepository;
import org.example.skymatesbackend.repository.PostRepository;
import org.example.skymatesbackend.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
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
        if (isPostLiked(postId, userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "已经点赞过该帖子");
        }
        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLike.setCreatedAt(LocalDateTime.now());
        postLikeRepository.save(postLike);
        updatePostLikesCount(postId, 1);
    }

    @Override
    @Transactional
    public void unlikePost(Long postId, Long userId) {
        if (!isPostLiked(postId, userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "未点赞过该帖子");
        }
        postLikeRepository.deleteByPostIdAndUserId(postId, userId);
        updatePostLikesCount(postId, -1);
    }


    @Override
    @Transactional
    public void likeComment(Long commentId, Long userId) {
        if (isCommentLiked(commentId, userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "已经点赞过该评论");
        }
        CommentLike commentLike = new CommentLike();
        commentLike.setCommentId(commentId);
        commentLike.setUserId(userId);
        commentLike.setCreatedAt(LocalDateTime.now());
        commentLikeRepository.save(commentLike);
        updateCommentLikesCount(commentId, 1);
    }

    @Override
    @Transactional
    public void unlikeComment(Long commentId, Long userId) {
        if (!isCommentLiked(commentId, userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "未点赞过该评论");
        }
        commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
        updateCommentLikesCount(commentId, -1);
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

    @Transactional(readOnly = true)
    public boolean isPostLiked(Long postId, Long userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }

    @Transactional(readOnly = true)
    public boolean isCommentLiked(Long commentId, Long userId) {
        return commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
    }

    private void updatePostLikesCount(Long postId, int delta) {
        for (int retry = 0; retry < 3; retry++) {
            Post post = postRepository.findById(postId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "帖子不存在"));
            int updatedRows = postRepository.updateLikesCountWithVersion(postId, post.getVersion(), delta);
            if (updatedRows > 0) return;
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "点赞操作出现并发冲突，请稍后重试");
    }

    private void updateCommentLikesCount(Long commentId, int delta) {
        for (int retry = 0; retry < 3; retry++) {
            Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "评论不存在"));
            int updatedRows = commentRepository.updateLikesCountWithVersion(commentId, comment.getVersion(), delta);
            if (updatedRows > 0) return;
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "点赞操作出现并发冲突，请稍后重试");
    }
}
