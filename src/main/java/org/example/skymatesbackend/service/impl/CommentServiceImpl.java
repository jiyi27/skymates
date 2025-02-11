package org.example.skymatesbackend.service.impl;

import org.example.skymatesbackend.converter.PageConverter;
import org.example.skymatesbackend.converter.UserConverter;
import org.example.skymatesbackend.dto.CommentDTO;
import org.example.skymatesbackend.dto.PageDTO;
import org.example.skymatesbackend.exception.BusinessException;
import org.example.skymatesbackend.model.Comment;
import org.example.skymatesbackend.model.Post;
import org.example.skymatesbackend.repository.CommentLikeRepository;
import org.example.skymatesbackend.repository.CommentRepository;
import org.example.skymatesbackend.repository.PostRepository;
import org.example.skymatesbackend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserConverter userConverter;
    private final PageConverter pageConverter;

    @Autowired
    public CommentServiceImpl(
            CommentRepository commentRepository,
            PostRepository postRepository,
            CommentLikeRepository commentLikeRepository,
            UserConverter userConverter,
            PageConverter pageConverter) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.userConverter = userConverter;
        this.pageConverter = pageConverter;
    }

    @Override
    public CommentDTO createComment(Long postId, Long userId, CommentDTO.CreateRequest request) {
        // 验证帖子是否存在
        postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));

        Long parentId = request.getParentId();

        if (parentId != null) {
            // 验证父评论是否存在
            Comment parentComment = commentRepository.findById(parentId)
                    .orElseThrow(() -> new BusinessException("父评论不存在"));

            // 验证父评论是否属于当前帖子
            if (!parentComment.getPostId().equals(postId)) {
                throw new BusinessException("父评论不属于该帖子，无法回复");
            }
        }

        // 创建新评论
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setParentId(parentId);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setStatus(1);
        comment.setLikesCount(0);
        comment.setRepliesCount(0);

        comment = commentRepository.save(comment);

        // 更新父评论和帖子评论数
        if (parentId != null) {
            updateCommentRepliesCount(parentId, 1);
        }
        updatePostCommentsCount(postId, 1);

        return convertToDTO(comment, false);
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException("评论不存在"));

        // 软删除评论
        int updatedRows = commentRepository.softDeleteComment(commentId, userId);
        if (updatedRows == 0) {
            throw new BusinessException("删除失败，可能评论不存在或无权限");
        }

        // 如果是回复评论，则减少父评论的回复数
        if (comment.getParentId() != null) {
            updateCommentRepliesCount(comment.getParentId(), -1);
        }

        // 任何删除的评论都需要减少帖子评论数
        updatePostCommentsCount(comment.getPostId(), -1);
    }

    /**
     * 获取指定帖子的评论（分页）
     *
     * @param postId       帖子 ID
     * @param page         当前页数
     * @param size         每页大小
     * @param currentUserId 当前用户 ID（用于获取点赞信息）
     * @return 评论分页数据
     */
    @Override
    @Transactional(readOnly = true)
    public PageDTO<CommentDTO> getPostComments(Long postId, int page, int size, Long currentUserId) {
        // 按创建时间降序排序，获取分页对象
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 查询顶级评论（不包含子评论）
        Page<Comment> commentPage = commentRepository.findTopLevelComments(postId, pageable);

        // 如果当前用户已登录，查询 TA 点赞过的评论 ID；否则返回空列表
        List<Long> likedCommentIds = (currentUserId != null)
                ? commentLikeRepository.findCommentIdsByUserId(currentUserId)
                : Collections.emptyList();

        // 使用 PageConverter 进行分页转换
        return pageConverter.convertToPageDTO(commentPage,
                comment -> convertToDTO(comment, likedCommentIds.contains(comment.getId())));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentReplies(Long commentId, Long currentUserId) {
        List<Comment> replies = commentRepository.findReplies(commentId);

        List<Long> likedCommentIds = currentUserId != null ?
                commentLikeRepository.findCommentIdsByUserId(currentUserId) :
                Collections.emptyList();

        return replies.stream()
                .map(reply -> convertToDTO(reply, likedCommentIds.contains(reply.getId())))
                .collect(Collectors.toList());
    }

    // 使用乐观锁更新帖子评论数
    private void updatePostCommentsCount(Long postId, int delta) {
        for (int retry = 0; retry < 3; retry++) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new BusinessException("帖子不存在"));

            int updatedRows = postRepository.updateCommentsCountWithVersion(postId, post.getVersion(), delta);
            if (updatedRows > 0) return; // 更新成功，退出重试
        }
        throw new RuntimeException("更新帖子评论数失败，可能存在并发冲突，请稍后重试");
    }

    // 使用乐观锁更新父评论的回复数
    private void updateCommentRepliesCount(Long commentId, int delta) {
        for (int retry = 0; retry < 3; retry++) {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new BusinessException("父评论不存在"));

            int updatedRows = commentRepository.updateRepliesCountWithVersion(commentId, comment.getVersion(), delta);
            if (updatedRows > 0) return; // 更新成功，退出重试
        }
        throw new RuntimeException("更新评论回复数失败，可能存在并发冲突，请稍后重试");
    }

    // 然后修改评论转换方法
    private CommentDTO convertToDTO(Comment comment, boolean isLiked) {
        if (comment == null) {
            return null;
        }
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPostId());
        dto.setAuthor(userConverter.convertToDTO(comment.getUser()));
        dto.setContent(comment.getContent());
        dto.setLikesCount(comment.getLikesCount());
        dto.setRepliesCount(comment.getRepliesCount());
        dto.setParentId(comment.getParentId());
        dto.setIsLiked(isLiked);
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }

}