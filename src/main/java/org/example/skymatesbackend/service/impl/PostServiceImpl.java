package org.example.skymatesbackend.service.impl;

import org.example.skymatesbackend.dto.PageDTO;
import org.example.skymatesbackend.dto.PostDTO;
import org.example.skymatesbackend.dto.UserDTO;
import org.example.skymatesbackend.model.Post;
import org.example.skymatesbackend.model.User;
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
import java.util.stream.Collectors;

@Service
@Transactional
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;

    @Autowired
    public PostServiceImpl(
            PostRepository postRepository,
            UserRepository userRepository,
            PostLikeRepository postLikeRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postLikeRepository = postLikeRepository;
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

    @Override
    @Transactional(readOnly = true)
    public PageDTO<PostDTO> getUserPosts(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPage = postRepository.findByUserId(userId, pageable);

        List<Long> likedPostIds = postLikeRepository.findPostIdsByUserId(userId);

        return convertToPageDTO(postPage, likedPostIds);
    }

    @Override
    @Transactional(readOnly = true)
    public PageDTO<PostDTO> getPosts(String keyword, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> postPage;
        if (StringUtils.hasText(keyword)) {
            postPage = postRepository.searchPosts(keyword, pageable);
        } else {
            postPage = postRepository.findAllActivePosts(pageable);
        }

        List<Long> likedPostIds = currentUserId != null ?
                postLikeRepository.findPostIdsByUserId(currentUserId) :
                Collections.emptyList();

        return convertToPageDTO(postPage, likedPostIds);
    }

    private PageDTO<PostDTO> convertToPageDTO(Page<Post> postPage, List<Long> likedPostIds) {
        List<PostDTO> postDTOs = postPage.getContent().stream()
                .map(post -> convertToDTO(post, likedPostIds.contains(post.getId())))
                .collect(Collectors.toList());

        PageDTO<PostDTO> pageDTO = new PageDTO<>();
        pageDTO.setContent(postDTOs);
        pageDTO.setPageNumber(postPage.getNumber());
        pageDTO.setPageSize(postPage.getSize());
        pageDTO.setTotalElements(postPage.getTotalElements());
        pageDTO.setTotalPages(postPage.getTotalPages());
        pageDTO.setHasNext(postPage.hasNext());

        return pageDTO;
    }

    private PostDTO convertToDTO(Post post, boolean isLiked) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthor(convertToDTO(post.getUser()));
        dto.setLikesCount(post.getLikesCount());
        dto.setCommentsCount(post.getCommentsCount());
        dto.setIsLiked(isLiked);
        dto.setCreatedAt(post.getCreatedAt());
        return dto;
    }

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
}
