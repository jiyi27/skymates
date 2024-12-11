// app/discuss/[postId]/page.tsx
import { notFound } from 'next/navigation';
import { posts, users, comments } from '../_data/mockData';
import CommentsList from "@/app/discuss/_components/CommentList";

interface PostPageProps {
    params: {
        postId: string;
    };
}

export default function PostPage({ params }: PostPageProps) {
    const post = posts.find((p) => p.id === params.postId);

    if (!post) {
        notFound();
    }

    const author = users.find((u) => u.id === post.authorId);
    const postComments = comments.filter((c) => c.postId === post.id);

    // 假设当前用户ID，实际应该从认证系统获取
    const currentUserId = "current-user-id";

    return (
        <div className="max-w-4xl mx-auto p-4">
            {/* 帖子标题 */}
            <h1 className="text-3xl font-bold mb-4">{post.title}</h1>

            {/* 帖子信息 */}
            <div className="flex items-center gap-4 mb-6 text-gray-600">
                <span>作者: {author?.name}</span>
                <span>发布时间: {new Date(post.createdAt).toLocaleString()}</span>
                <span>点赞数: {post.likesCount}</span>
            </div>

            {/* 帖子内容 */}
            <div className="prose max-w-none mb-8">
                <p>{post.content}</p>
            </div>

            {/* 评论区 */}
            <CommentsList
                comments={postComments}
                currentUserId={currentUserId}
            />
        </div>
    );
}