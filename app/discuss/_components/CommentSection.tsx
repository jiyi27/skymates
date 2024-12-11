'use client';

import React, { useState } from 'react';
import { Comment, User } from '@/app/discuss/_data/mockData';  // 引入之前定义的类型
import CommentCard from './CommentCard';  // 引入之前创建的评论卡片组件
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";

// 组件props类型定义
type CommentSectionProps = {
    comments: Comment[];
    currentUser: User;
    postId: string;
}

const CommentSection = ({ comments, currentUser, postId }: CommentSectionProps) => {
    // 状态管理
    const [commentText, setCommentText] = useState('');  // 评论输入内容
    const [replyToId, setReplyToId] = useState<string | null>(null);  // 要回复的评论ID
    const [replyToComment, setReplyToComment] = useState<Comment | null>(null);  // 要回复的评论详情
    const [localComments, setLocalComments] = useState<Comment[]>(comments);  // 本地评论状态

    // 处理评论提交
    const handleSubmitComment = async () => {
        if (!commentText.trim()) return;

        try {
            // 构建新评论数据
            const newComment: Comment = {
                id: `temp-${Date.now()}`,  // 临时ID，实际应由后端生成
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

            // TODO: 发送API请求保存评论
            // const response = await fetch('/api/comments', {
            //   method: 'POST',
            //   body: JSON.stringify(newComment),
            // });
            // const savedComment = await response.json();

            // 更新本地状态
            setLocalComments(prev => [newComment, ...prev]);

            // 清空输入框和回复状态
            setCommentText('');
            setReplyToId(null);
            setReplyToComment(null);
        } catch (error) {
            console.error('Failed to submit comment:', error);
            // TODO: 显示错误提示
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

    // 处理取消回复
    const handleCancelReply = () => {
        setReplyToId(null);
        setReplyToComment(null);
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
                    {/* 回复提示 */}
                    {replyToComment && (
                        <div className="mb-2 p-2 bg-gray-50 rounded-md text-sm text-gray-600 flex justify-between items-center">
              <span>
                回复 <span className="font-medium">{replyToComment.user.name}</span>: {replyToComment.content}
              </span>
                            <Button
                                variant="ghost"
                                size="sm"
                                onClick={handleCancelReply}
                                className="text-gray-500 hover:text-gray-700"
                            >
                                取消回复
                            </Button>
                        </div>
                    )}

                    {/* 评论输入框 */}
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

            {/* 评论列表 */}
            <div className="space-y-4">
                {localComments.map((comment) => (
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