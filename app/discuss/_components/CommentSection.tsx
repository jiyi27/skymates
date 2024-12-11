'use client';

import React, { useState, useMemo } from 'react';
import { Comment, User } from '@/app/discuss/_data/mockData';
import CommentCard from './CommentCard';
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"

// 定义排序类型
type SortType = 'hot' | 'latest';

type CommentSectionProps = {
    comments: Comment[];
    currentUser: User;
    postId: string;
}

const CommentSection = ({ comments, currentUser, postId }: CommentSectionProps) => {
    // 之前的状态
    const [commentText, setCommentText] = useState('');
    const [replyToId, setReplyToId] = useState<string | null>(null);
    const [replyToComment, setReplyToComment] = useState<Comment | null>(null);
    const [localComments, setLocalComments] = useState<Comment[]>(comments);

    // 新增排序状态，默认为热门
    const [sortType, setSortType] = useState<SortType>('hot');

    // 使用 useMemo 计算排序后的评论列表，避免不必要的重新计算
    const sortedComments = useMemo(() => {
        const commentsCopy = [...localComments];

        switch (sortType) {
            case 'hot':
                // 按点赞数降序排序，点赞数相同时按时间降序
                return commentsCopy.sort((a, b) => {
                    if (b.likesCount !== a.likesCount) {
                        return b.likesCount - a.likesCount;
                    }
                    return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
                });
            case 'latest':
                // 按创建时间降序排序
                return commentsCopy.sort((a, b) =>
                    new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
                );
            default:
                return commentsCopy;
        }
    }, [localComments, sortType]);

    // 处理排序变化
    const handleSortChange = (value: SortType) => {
        setSortType(value);
    };

    // 提交新评论时，需要考虑当前的排序方式
    const handleSubmitComment = async () => {
        if (!commentText.trim()) return;

        try {
            const newComment: Comment = {
                id: `temp-${Date.now()}`,
                postId,
                userId: currentUser.id,
                content: commentText,
                likesCount: 0,
                replyToId: replyToId,
                replyTo: replyToId ? {
                    id: replyToComment!.id,
                    content: replyToComment!.content,
                    userId: replyToComment!.userId,
                    username: replyToComment!.user.username,
                } : null,
                user: currentUser,
                isLiked: false,
                createdAt: new Date().toISOString(),
                updatedAt: new Date().toISOString(),
            };

            // TODO: API 调用
            // const response = await fetch('/api/comments', {
            //   method: 'POST',
            //   body: JSON.stringify(newComment),
            // });
            // const savedComment = await response.json();

            // 更新本地状态
            setLocalComments(prev => [newComment, ...prev]);
            setCommentText('');
            setReplyToId(null);
            setReplyToComment(null);

            // 如果当前是按热门排序，可能需要调整新评论的位置
            if (sortType === 'hot') {
                setSortType('latest');
                // 可以选择显示一个提示："已切换到最新排序以显示您的评论"
            }
        } catch (error) {
            console.error('Failed to submit comment:', error);
        }
    };


    // 处理回复按钮点击
    const handleReply = (commentId: string) => {
        const commentToReply = localComments.find(c => c.id === commentId);
        if (commentToReply) {
            setReplyToId(commentId);
            setReplyToComment(commentToReply);
            // 让输入框获得焦点
            document.querySelector('textarea')?.focus();
        }
    };

    // 处理点赞功能
    const handleLike = async (commentId: string) => {
        try {
            // TODO: 发送API请求处理点赞
            // const response = await fetch(`/api/comments/${commentId}/like`, {
            //   method: 'POST',
            // });

            // 更新本地状态
            setLocalComments(prev => prev.map(comment => {
                if (comment.id === commentId) {
                    return {
                        ...comment,
                        isLiked: !comment.isLiked,
                        likesCount: comment.isLiked ? comment.likesCount - 1 : comment.likesCount + 1,
                    };
                }
                return comment;
            }));
        } catch (error) {
            console.error('Failed to like comment:', error);
            // TODO: 显示错误提示
        }
    };

    // 处理删除评论
    const handleDelete = async (commentId: string) => {
        try {
            // TODO: 发送API请求删除评论
            // await fetch(`/api/comments/${commentId}`, {
            //   method: 'DELETE',
            // });

            // 更新本地状态
            setLocalComments(prev => prev.filter(comment => comment.id !== commentId));
        } catch (error) {
            console.error('Failed to delete comment:', error);
            // TODO: 显示错误提示
        }
    };

    return (
        <div className="space-y-4 pb-20">
            {/* 评论输入区域 */}
            <div className="flex gap-4">
                <Avatar className="h-8 w-8">
                    <AvatarImage src={currentUser.avatar || ''} />
                    <AvatarFallback>{currentUser.name[0]}</AvatarFallback>
                </Avatar>
                <div className="flex-1">
                    {replyToComment && (
                        <div className="mb-2 p-2 bg-gray-50 rounded-md text-sm text-gray-600 flex justify-between items-center">
              <span>
                回复 <span className="font-medium">{replyToComment.user.name}</span>: {replyToComment.content}
              </span>
                            <Button
                                variant="ghost"
                                size="sm"
                                onClick={() => {
                                    setReplyToId(null);
                                    setReplyToComment(null);
                                }}
                                className="text-gray-500 hover:text-gray-700"
                            >
                                取消回复
                            </Button>
                        </div>
                    )}

                    <Textarea
                        value={commentText}
                        onChange={(e) => setCommentText(e.target.value)}
                        placeholder={replyToComment ? "输入回复内容..." : "写下你的评论..."}
                        className="min-h-[100px]"
                    />
                    <div className="mt-2 flex justify-end">
                        <Button
                            onClick={handleSubmitComment}
                            disabled={!commentText.trim()}
                        >
                            {replyToComment ? "回复" : "发布评论"}
                        </Button>
                    </div>
                </div>
            </div>

            {/* 排序选择器和评论计数 */}
            <div className="flex justify-between items-center py-4">
        <span className="text-sm text-gray-600">
          共 {localComments.length} 条评论
        </span>
                <Select
                    value={sortType}
                    onValueChange={handleSortChange}
                >
                    <SelectTrigger className="w-32">
                        <SelectValue placeholder="选择排序方式" />
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="hot">热门排序</SelectItem>
                        <SelectItem value="latest">最新排序</SelectItem>
                    </SelectContent>
                </Select>
            </div>

            {/* 评论列表 */}
            <div className="space-y-4">
                {sortedComments.map((comment) => (
                    <CommentCard
                        key={comment.id}
                        comment={comment}
                        onLike={handleLike}
                        onReply={handleReply}
                        onDelete={handleDelete}
                        currentUserId={currentUser.id}
                    />
                ))}
            </div>
        </div>
    );
};

export default CommentSection;