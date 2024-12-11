'use client';

import React from 'react';
import { Heart, MessageCircle, MoreVertical } from 'lucide-react';
import {
    Card,
    CardContent,
} from "@/components/ui/card"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Button } from "@/components/ui/button"
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar"

import { Comment } from "@/app/discuss/_data/mockData"

// 评论卡片的props类型定义
type CommentCardProps = {
    comment: Comment;
    onLike: (commentId: string) => void;
    onReply: (commentId: string) => void;
    onDelete?: (commentId: string) => void;
    currentUserId?: string;
}

const CommentCard = ({
                         comment,
                         onLike,
                         onReply,
                         onDelete,
                         currentUserId
                     }: CommentCardProps) => {
    // 获取评论创建时间的相对时间
    const getRelativeTime = (dateString: string) => {
        const date = new Date(dateString);
        const now = new Date();
        const diff = now.getTime() - date.getTime();

        const minutes = Math.floor(diff / 60000);
        const hours = Math.floor(minutes / 60);
        const days = Math.floor(hours / 24);

        if (days > 0) return `${days}天前`;
        if (hours > 0) return `${hours}小时前`;
        if (minutes > 0) return `${minutes}分钟前`;
        return '刚刚';
    };

    // 获取用户名首字母作为头像备选显示
    const getInitials = (name: string) => {
        return name.charAt(0).toUpperCase();
    };

    return (
        <Card className="mb-4 last:mb-0">
            <CardContent className="pt-6">
                {/* 评论头部：用户信息和操作按钮 */}
                <div className="flex justify-between items-start mb-2">
                    <div className="flex items-center gap-2">
                        <Avatar className="h-8 w-8">
                            <AvatarImage src={comment.user.avatarUrl || ''} alt={comment.user.name} />
                            <AvatarFallback>{getInitials(comment.user.name)}</AvatarFallback>
                        </Avatar>
                        <div>
                            <p className="font-medium text-sm">{comment.user.username}</p>
                            <p className="text-xs text-gray-500">{getRelativeTime(comment.createdAt)}</p>
                        </div>
                    </div>

                    {/* 更多操作下拉菜单 */}
                    {currentUserId === comment.userId && (
                        <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                                <Button variant="ghost" size="icon" className="h-8 w-8">
                                    <MoreVertical className="h-4 w-4" />
                                </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="end">
                                <DropdownMenuItem
                                    className="text-destructive"
                                    onClick={() => onDelete?.(comment.id)}
                                >
                                    删除评论
                                </DropdownMenuItem>
                            </DropdownMenuContent>
                        </DropdownMenu>
                    )}
                </div>

                {/* 回复引用 */}
                {comment.replyTo && (
                    <div className="ml-10 mb-2 p-2 bg-gray-50 rounded-md text-sm text-gray-600">
                        <span className="font-medium">{comment.replyTo.username}</span>: {comment.replyTo.content}
                    </div>
                )}

                {/* 评论内容 */}
                <div className="ml-10 mb-3">
                    <p className="text-sm text-gray-800">{comment.content}</p>
                </div>

                {/* 评论操作栏 */}
                <div className="ml-10 flex items-center gap-4">
                    <Button
                        variant="ghost"
                        size="sm"
                        className={`flex items-center gap-1 ${comment.isLiked ? 'text-red-500' : ''}`}
                        onClick={() => onLike(comment.id)}
                    >
                        <Heart className="h-4 w-4" fill={comment.isLiked ? "currentColor" : "none"} />
                        <span className="text-xs">{comment.likesCount}</span>
                    </Button>

                    <Button
                        variant="ghost"
                        size="sm"
                        className="flex items-center gap-1"
                        onClick={() => onReply(comment.id)}
                    >
                        <MessageCircle className="h-4 w-4" />
                        <span className="text-xs">回复</span>
                    </Button>
                </div>
            </CardContent>
        </Card>
    );
};

export default CommentCard;