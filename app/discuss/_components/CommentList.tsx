"use client";

import { useState } from 'react';
import { Comment } from '../_data/mockData';
import CommentCard from './CommentCard';

interface CommentsProps {
    comments: Comment[];
    currentUserId: string;
}

export default function CommentsList({ comments, currentUserId }: CommentsProps) {
    // 评论相关状态和处理函数
    const [commentsList, setCommentsList] = useState<Comment[]>(comments);

    // 处理点赞
    const handleLike = (commentId: string) => {
        setCommentsList(prevComments =>
            prevComments.map(comment => {
                if (comment.id === commentId) {
                    return {
                        ...comment,
                        isLiked: !comment.isLiked,
                        likesCount: comment.isLiked
                            ? comment.likesCount - 1
                            : comment.likesCount + 1
                    };
                }
                return comment;
            })
        );
    };

    // 处理回复
    const handleReply = (commentId: string) => {
        // TODO: 实现回复功能，可能需要打开一个回复框或模态框
        console.log('Reply to comment:', commentId);
    };

    // 处理删除
    const handleDelete = (commentId: string) => {
        setCommentsList(prevComments =>
            prevComments.filter(comment => comment.id !== commentId)
        );
    };

    return (
        <div className="mt-8">
            <h2 className="text-2xl font-bold mb-4">
                评论 ({commentsList.length})
            </h2>
            <div className="space-y-4">
                {commentsList.map((comment) => (
                    <CommentCard
                        key={comment.id}
                        comment={comment}
                        currentUserId={currentUserId}
                        onLike={handleLike}
                        onReply={handleReply}
                        onDelete={handleDelete}
                    />
                ))}
            </div>
        </div>
    );
}