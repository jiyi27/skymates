// app/discuss/page.tsx
import { posts, users } from './_data/mockData';
import { PostList } from './_components/PostList';

export default function DiscussPage() {
    return (
        <div className="max-w-4xl mx-auto py-8 px-4">
            <PostList
                initialPosts={posts}
                users={users}
            />
        </div>
    );
}