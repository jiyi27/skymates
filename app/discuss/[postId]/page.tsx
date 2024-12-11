// app/discuss/[postId]/page.tsx
import { notFound } from 'next/navigation';
import { posts, users, comments } from '../_data/mockData';
import CommentSection from "@/app/discuss/_components/CommentSection";

interface PostPageProps {
    params: {
        postId: string;
    };
}

// Next.js 路由参数的处理为异步，要等待 Next.js 路由系统解析完 URL 参数后，才能确保 params 对象包含正确的值
// 所以声明为异步函数，等待解析 Next.js 已经帮我们做了
export default async function PostPage({ params }: PostPageProps) {
    const post = posts.find((p) => p.id === params.postId);

    if (!post) {
        notFound();
    }

    const author = users.find((u) => u.id === post.authorId);

    // 假设当前用户，实际应该从认证系统获取
    const currentUser = {
        id: '1',
        username: 'TechGuru',
        name: '张三',
        email: 'tech.guru@example.com',
        avatar: '/avatars/1.png',
        createdAt: '2024-01-01T00:00:00Z',
    };

    const currentComments = comments.filter((c) => c.postId === post.id);

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
            <CommentSection
                comments={currentComments}
                currentUser={currentUser}
                postId="1"
            />
        </div>
    );
}