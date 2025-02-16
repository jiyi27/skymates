package org.example.skymatesbackend.service.impl;

import org.example.skymatesbackend.converter.PageConverter;
import org.example.skymatesbackend.converter.UserConverter;
import org.example.skymatesbackend.dto.PageDTO;
import org.example.skymatesbackend.dto.PostDTO;
import org.example.skymatesbackend.model.Post;
import org.example.skymatesbackend.repository.PostLikeRepository;
import org.example.skymatesbackend.repository.PostRepository;
import org.example.skymatesbackend.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.example.skymatesbackend.exception.BusinessException;
import org.example.skymatesbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserConverter userConverter;
    private final PageConverter pageConverter;

    @Autowired
    public PostServiceImpl(
            PostRepository postRepository,
            UserRepository userRepository,
            PostLikeRepository postLikeRepository,
            UserConverter userConverter,
            PageConverter pageConverter) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postLikeRepository = postLikeRepository;
        this.userConverter = userConverter;
        this.pageConverter = pageConverter;
    }

    @Override
    public PostDTO createPost(Long userId, PostDTO.CreateRequest request) {
        // 验证用户是否存在
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 创建帖子实体
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setStatus(1);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        // 保存帖子
        post = postRepository.save(post);

        return convertToDTO(post, false);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDTO getPostById(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));

        if (post.getStatus() != 1) {
            throw new BusinessException("帖子已删除");
        }

        boolean isLiked = currentUserId != null &&
                postLikeRepository.existsByPostIdAndUserId(postId, currentUserId);

        return convertToDTO(post, isLiked);
    }

    @Override
    public PostDTO updatePost(Long postId, Long userId, PostDTO.UpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));

        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("没有权限修改此帖子");
        }

        if (post.getStatus() != 1) {
            throw new BusinessException("帖子已删除");
        }

        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        post.setUpdatedAt(LocalDateTime.now());

        post = postRepository.save(post);

        return convertToDTO(post, postLikeRepository.existsByPostIdAndUserId(postId, userId));
    }

    @Override
    public void deletePost(Long postId, Long userId) {
        int affected = postRepository.softDeletePost(postId, userId);
        if (affected == 0) {
            throw new BusinessException("删除失败，帖子不存在或没有权限");
        }
    }

    /**
     * 获取指定用户发布的帖子（分页）
     *
     * @param userId 用户 ID
     * @param page   当前页数
     * @param size   每页大小
     * @return 用户的帖子分页数据
     */
    @Override
    @Transactional(readOnly = true)
    public PageDTO<PostDTO> getUserPosts(Long userId, int page, int size) {
        // 按创建时间降序排序，获取分页对象
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 查询该用户发布的帖子
        Page<Post> postPage = postRepository.findByUserIdAndStatus(userId, 1, pageable);

        // 查询该用户点赞过的帖子 ID
        List<Long> likedPostIds = postLikeRepository.findPostIdsByUserId(userId);

        return pageConverter.convertToPageDTO(postPage,
                post -> convertToDTO(post, likedPostIds.contains(post.getId())));
    }

    /**
     * 获取帖子列表（支持关键字搜索，分页）
     *
     * @param keyword       搜索关键字（可选）
     * @param page          当前页数
     * @param size          每页大小
     * @param currentUserId 当前用户 ID（用于获取点赞信息）
     * @return 帖子分页数据
     */
    @Override
    @Transactional(readOnly = true)
    public PageDTO<PostDTO> getPosts(String keyword, int page, int size, Long currentUserId) {
        // 按创建时间降序排序，获取分页对象
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> postPage;
        if (StringUtils.hasText(keyword)) {
            // 关键词搜索
            postPage = postRepository.searchPosts(keyword, pageable);
        } else {
            // 查询所有活跃帖子
            postPage = postRepository.findAllActivePosts(pageable);
        }

        // 如果当前用户已登录，查询 TA 点赞过的帖子 ID；否则返回空列表
        List<Long> likedPostIds = (currentUserId != null)
                ? postLikeRepository.findPostIdsByUserId(currentUserId)
                : Collections.emptyList();

        // 使用 PageConverter 进行分页转换
        return pageConverter.convertToPageDTO(postPage,
                post -> convertToDTO(post, likedPostIds.contains(post.getId())));
    }

    private PostDTO convertToDTO(Post post, boolean isLiked) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthor(userConverter.convertToDTO(post.getUser()));
        dto.setLikesCount(post.getLikesCount());
        dto.setCommentsCount(post.getCommentsCount());
        dto.setIsLiked(isLiked);
        dto.setCreatedAt(post.getCreatedAt());
        return dto;
    }
}
