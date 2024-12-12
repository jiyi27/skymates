// app/discuss/_components/PostCard.tsx
"use client";

import React from 'react';
import Link from "next/link";
import { Heart, MessageCircle, Clock } from 'lucide-react';
import { formatDistanceToNow } from 'date-fns';
import { zhCN } from 'date-fns/locale';
import {
    Card,
    CardHeader,
    CardContent,
    CardFooter,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import type { Post } from '../_data/mockData';

// PostCard 组件
interface PostCardProps {
    post: Post;
    onLike: (postId: string) => void;
}

export const PostCard = ({ post, onLike }: PostCardProps) => {
    // 使用 date-fns 格式化时间
    const timeAgo = formatDistanceToNow(new Date(post.createdAt), {
        addSuffix: true,
        locale: zhCN,
    });

    // 阻止点赞按钮触发链接跳转
    const handleLikeClick = (e: React.MouseEvent) => {
        e.preventDefault();
        onLike(post.id);
    };

    return (
        <Link href={`/discuss/${post.id}`}>
            <Card className="mb-4 hover:shadow-lg transition-shadow">
                <CardHeader className="pb-2">
                    <div className="flex items-center space-x-3">
                        <Avatar>
                            <AvatarImage src={post.user.avatar || ''} alt={post.user.name} />
                            <AvatarFallback>{post.user.name[0]}</AvatarFallback>
                        </Avatar>
                        <div className="flex flex-col">
                            <span className="font-medium text-sm">{post.user.name}</span>
                            <div className="flex items-center text-sm text-muted-foreground">
                                <Clock className="w-4 h-4 mr-1" />
                                <span>{timeAgo}</span>
                            </div>
                        </div>
                    </div>
                </CardHeader>

                <CardContent>
                    <h2 className="text-xl font-semibold mb-2">{post.title}</h2>
                    <p className="text-muted-foreground line-clamp-3">{post.content}</p>
                </CardContent>

                <CardFooter>
                    <div className="flex items-center space-x-4">
                        <Button
                            variant="ghost"
                            size="sm"
                            className={post.isLiked ? 'text-red-500' : ''}
                            onClick={handleLikeClick}
                        >
                            <Heart className={`w-5 h-5 mr-1 ${post.isLiked ? 'fill-current' : ''}`} />
                            <span>{post.likesCount}</span>
                        </Button>

                        <Button variant="ghost" size="sm">
                            <MessageCircle className="w-5 h-5 mr-1" />
                            <span>{post.commentsCount}</span>
                        </Button>
                    </div>
                </CardFooter>
            </Card>
        </Link>
    );
};
