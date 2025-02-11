package org.example.skymatesbackend.service.impl;

import org.example.skymatesbackend.dto.CommentDTO;
import org.example.skymatesbackend.dto.PageDTO;
import org.example.skymatesbackend.dto.UserDTO;
import org.example.skymatesbackend.exception.BusinessException;
import org.example.skymatesbackend.model.Comment;
import org.example.skymatesbackend.model.Post;
import org.example.skymatesbackend.model.User;
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

    @Autowired
    public CommentServiceImpl(
            CommentRepository commentRepository,
            PostRepository postRepository,
            CommentLikeRepository commentLikeRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

    @Override
    public CommentDTO createComment(Long postId, Long userId, CommentDTO.CreateRequest request) {
        // 验证帖子是否存在
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));

        // 如果是回复评论，验证父评论是否存在
        if (request.getParentId() != null) {
            Comment parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException("父评论不存在"));

            // 验证父评论是否属于当前帖子
            if (!parentComment.getPostId().equals(postId)) {
                throw new BusinessException("父评论不属于当前帖子");
            }
        }

        // 创建评论实体
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setParentId(request.getParentId());
        comment.setContent(request.getContent());
        comment.setStatus(1);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        // 保存评论
        comment = commentRepository.save(comment);

        // 更新帖子评论数
        postRepository.updateCommentsCount(postId, 1);

        // 如果是回复评论，更新父评论的回复数
        if (request.getParentId() != null) {
            commentRepository.updateRepliesCount(request.getParentId(), 1);
        }

        return convertToDTO(comment, false);
    }

    @Override
    @Transactional(readOnly = true)
    public PageDTO<CommentDTO> getPostComments(Long postId, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPage = commentRepository.findTopLevelComments(postId, pageable);

        List<Long> likedCommentIds = currentUserId != null ?
                commentLikeRepository.findCommentIdsByUserId(currentUserId) :
                Collections.emptyList();

        return convertToPageDTO(commentPage, likedCommentIds);
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

    @Override
    public void deleteComment(Long commentId, Long userId) {
        int affected = commentRepository.softDeleteComment(commentId, userId);
        if (affected == 0) {
            throw new BusinessException("删除失败，评论不存在或没有权限");
        }

        // 获取评论信息
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException("评论不存在"));

        // 更新帖子评论数
        postRepository.updateCommentsCount(comment.getPostId(), -1);

        // 如果是回复，更新父评论的回复数
        if (comment.getParentId() != null) {
            commentRepository.updateRepliesCount(comment.getParentId(), -1);
        }
    }

    private PageDTO<CommentDTO> convertToPageDTO(Page<Comment> commentPage, List<Long> likedCommentIds) {
        List<CommentDTO> commentDTOs = commentPage.getContent().stream()
                .map(comment -> convertToDTO(comment, likedCommentIds.contains(comment.getId())))
                .collect(Collectors.toList());

        PageDTO<CommentDTO> pageDTO = new PageDTO<>();
        pageDTO.setContent(commentDTOs);
        pageDTO.setPageNumber(commentPage.getNumber());
        pageDTO.setPageSize(commentPage.getSize());
        pageDTO.setTotalElements(commentPage.getTotalElements());
        pageDTO.setTotalPages(commentPage.getTotalPages());
        pageDTO.setHasNext(commentPage.hasNext());

        return pageDTO;
    }

    // 在 CommentServiceImpl 中添加用户转换方法
    private UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    // 然后修改评论转换方法
    private CommentDTO convertToDTO(Comment comment, boolean isLiked) {
        if (comment == null) {
            return null;
        }
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPostId());
        dto.setAuthor(convertToDTO(comment.getUser())); // 现在可以正确调用用户转换方法
        dto.setContent(comment.getContent());
        dto.setLikesCount(comment.getLikesCount());
        dto.setRepliesCount(comment.getRepliesCount());
        dto.setParentId(comment.getParentId());
        dto.setIsLiked(isLiked);
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }

}