// app/discuss/_components/PostCard.tsx
"use client";

import { UserCircle, Heart, MessageCircle, Clock } from 'lucide-react';
import { formatDistanceToNow } from 'date-fns';
import { zhCN } from 'date-fns/locale';
import type { Post, User } from '../_data/mockData';
import Link from "next/link";

interface PostCardProps {
    post: Post;
    author: User;
    onLike: (postId: string) => void;
}

export const PostCard = ({ post, author, onLike }: PostCardProps) => {
    const timeAgo = formatDistanceToNow(new Date(post.createdAt), {
        addSuffix: true,
        locale: zhCN,
    });

    return (
        <Link href={`/discuss/${post.id}`}>
            <div className="bg-white rounded-lg shadow-md p-6 mb-4 transition-shadow hover:shadow-lg">
            <div className="flex items-center mb-3">
                <div className="flex items-center flex-1">
                    {author.avatar ? (
                        <img
                            src={author.avatar}
                            alt={author.name}
                            className="w-10 h-10 rounded-full mr-3"
                        />
                    ) : (
                        <UserCircle className="w-10 h-10 text-gray-400 mr-3" />
                    )}
                    <div>
                        <h3 className="font-medium text-gray-900">{author.name}</h3>
                        <div className="flex items-center text-sm text-gray-500">
                            <Clock className="w-4 h-4 mr-1" />
                            <span>{timeAgo}</span>
                        </div>
                    </div>
                </div>
            </div>

            <div className="mb-4">
                <h2 className="text-xl font-semibold mb-2">{post.title}</h2>
                <p className="text-gray-600 line-clamp-3">{post.content}</p>
            </div>

            <div className="flex items-center text-gray-500">
                <button
                    onClick={() => onLike(post.id)}
                    className={`flex items-center mr-6 hover:text-red-500 transition-colors ${
                        post.isLiked ? 'text-red-500' : ''
                    }`}
                >
                    <Heart className={`w-5 h-5 mr-1 ${post.isLiked ? 'fill-current' : ''}`} />
                    <span>{post.likesCount}</span>
                </button>

                <div className="flex items-center">
                    <MessageCircle className="w-5 h-5 mr-1" />
                    <span>{post.commentsCount}</span>
                </div>
            </div>
        </div>
        </Link>
    );
};
