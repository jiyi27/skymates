// app/discuss/_components/PostListClient.tsx
"use client";

import { useState } from 'react';
import { PostCard } from './PostCard';
import { PostForm } from './PostForm';
import { SortControl } from './SortControl';
import type { SortOption } from './types';
import type { Post, User } from '../_data/mockData';

interface PostListProps {
    initialPosts: Post[];
    users: User[];
}

export const PostList = ({ initialPosts, users }: PostListProps) => {
    const [posts, setPosts] = useState(initialPosts);
    const [sortOption, setSortOption] = useState<SortOption>('latest');

    const handleLike = (postId: string) => {
        setPosts(prevPosts =>
            prevPosts.map(post =>
                post.id === postId
                    ? {
                        ...post,
                        isLiked: !post.isLiked,
                        likesCount: post.isLiked ? post.likesCount - 1 : post.likesCount + 1,
                    }
                    : post
            )
        );
    };

    const handleNewPost = (title: string, content: string) => {
        const newPost: Post = {
            id: `p${Date.now()}`, // 实际应用中应该由后端生成
            title,
            content,
            authorId: 'u1', // 实际应用中应该使用当前登录用户的ID
            createdAt: new Date().toISOString(),
            likesCount: 0,
            commentsCount: 0,
            isLiked: false,
            comments: [],
            user: users[0],
            updatedAt: new Date().toISOString(),
        };

        setPosts(prevPosts => [newPost, ...prevPosts]);
    };

    const handleSort = (option: SortOption) => {
        setSortOption(option);

        const sortedPosts = [...posts].sort((a, b) => {
            switch (option) {
                case 'latest':
                    return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
                case 'hot':
                    // 这里用一个简单的热度算法：点赞数 * 2 + 评论数
                    const hotA = a.likesCount * 2 + a.commentsCount;
                    const hotB = b.likesCount * 2 + b.commentsCount;
                    return hotB - hotA;
                case 'mostLiked':
                    return b.likesCount - a.likesCount;
                default:
                    return 0;
            }
        });

        setPosts(sortedPosts);
    };

    const findUser = (userId: string) => {
        return users.find(user => user.id === userId) || {
            id: userId,
            name: '未知用户',
            avatar: '',
            email: '',
            createdAt: '',
            username: '',
        };
    };

    return (
        <div>
            <PostForm onSubmit={handleNewPost} />
            <SortControl currentSort={sortOption} onSortChange={handleSort} />
            <div className="space-y-4 pb-20">
                {posts.map(post => (
                    <PostCard
                        key={post.id}
                        post={post}
                        author={findUser(post.authorId)}
                        onLike={handleLike}
                    />
                ))}
            </div>
        </div>
    );
};