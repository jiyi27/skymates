// app/discuss/_components/PostListClient.tsx
"use client";

import React from "react";
import type {SortOption} from "@/app/discuss/_components/types";
import {PostCard} from "@/app/discuss/_components/PostCard";
import {PostForm} from "@/app/discuss/_components/PostForm";
import {SortControl} from "@/app/discuss/_components/SortControl";
import {Post, User} from "@/app/discuss/_data/mockData";

interface PostListProps {
    initialPosts: Post[];
    users: User[];
}

export const PostList = ({ initialPosts, users }: PostListProps) => {
    const sortFunctions = React.useMemo(() => ({
        latest: (a: Post, b: Post) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
        hot: (a: Post, b: Post) => (b.likesCount * 2 + b.commentsCount) - (a.likesCount * 2 + a.commentsCount),
        mostLiked: (a: Post, b: Post) => b.likesCount - a.likesCount
    }), []);

    const [posts, setPosts] = React.useState(() => {
        return [...initialPosts].sort(sortFunctions.hot);
    });
    const [sortOption, setSortOption] = React.useState<SortOption>('hot');

    const handleSort = React.useCallback((option: SortOption) => {
        setSortOption(option);
        setPosts(prevPosts => {
            const sortFn = sortFunctions[option] || (() => 0);
            return [...prevPosts].sort(sortFn);
        });
    }, [sortFunctions]);

    const handleLike = React.useCallback((postId: string) => {
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
    }, []);

    const handleNewPost = React.useCallback((title: string, content: string) => {
        const newPost: Post = {
            id: `p${Date.now()}`,
            authorId: 'u1',
            title,
            content,
            likesCount: 0,
            commentsCount: 0,
            user: users[0],
            isLiked: false,
            comments: [],
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
        };

        setPosts(prevPosts => [newPost, ...prevPosts]);
    }, [users]);

    return (
        <div className="max-w-3xl mx-auto">
            <PostForm onSubmit={handleNewPost} />
            <SortControl currentSort={sortOption} onSortChange={handleSort} />
            <div className="space-y-4 pb-20">
                {posts.map(post => (
                    <PostCard
                        key={post.id}
                        post={post}
                        onLike={handleLike}
                    />
                ))}
            </div>
        </div>
    );
};