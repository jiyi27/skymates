import { notFound } from "next/navigation";
import { posts, users, comments } from "../_data/mockData";
import CommentSection from "@/app/discuss/_components/CommentSection";
import { Heart } from "lucide-react";
import React from "react";

// In Next.js version 15 you must await the params, as they are now a promise.
// `params` should be awaited before using its properties.
export default async function PostDetailPage({
    params,
}: {
    params: Promise<{ postId: string }>;
}) {
    const { postId } = await params;
    const post = posts.find((p) => p.id === postId);

    if (!post) {
        notFound();
    }

    const author = users.find((u) => u.id === post.authorId);

    // 假设当前用户，实际应该从认证系统获取
    const currentUser = {
        id: "1",
        username: "TechGuru",
        name: "张三",
        email: "tech.guru@example.com",
        avatar: "/avatars/1.png",
        createdAt: "2024-01-01T00:00:00Z",
    };

    const currentComments = comments.filter((c) => c.postId === post.id);

    return (
        <div className="max-w-4xl mx-auto p-4 md:p-6">
            <h1 className="text-3xl font-bold mb-6 md:mb-8">{post.title}</h1>

            {/* 内容 - 增加下方间距 */}
            <div className="prose max-w-none mb-4 md:mb-6">
                <p>{post.content}</p>
            </div>

            {/* 帖子信息 - 保持简洁，仅增加间距 */}
            <div className="flex items-center gap-4 mb-6 md:mb-8 text-gray-600 text-sm">
                <span>作者: {author?.name}</span>
                <span>
                    发布时间: {new Date(post.createdAt).toLocaleString()}
                </span>

                <div className="flex items-center gap-1">
                    <Heart className="w-4 h-4 fill-current" />
                    <span>{post.likesCount}</span>
                </div>
            </div>

            {/* 评论区 */}
            <CommentSection
                comments={currentComments}
                currentUser={currentUser}
                postId="1"
            />
        </div>
    );
}
