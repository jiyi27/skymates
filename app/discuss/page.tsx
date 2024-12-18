// app/discuss/page.tsx
import { posts, users } from './_data/mockData';
import { PostList } from './_components/PostList';

export default function DiscussPage() {
    // 按热度排序 post
    const sortedPosts = [...posts].sort((a, b) => {
        return (b.likesCount * 2 + b.commentsCount) - (a.likesCount * 2 + a.commentsCount);
    });

    return (
        <div className="max-w-4xl mx-auto py-8 px-4 mb-20">
            <PostList
                initialPosts={sortedPosts}
                users={users}
            />
        </div>
    );
}