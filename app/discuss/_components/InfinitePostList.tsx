// // app/discuss/_components/InfinitePostList.tsx
// import { useState, useCallback, useRef, useEffect } from 'react';
// import { PostCard } from './PostCard';
// import type { Post, User } from '../_data/mockData';
// import { Loader2 } from 'lucide-react';
//
// interface InfinitePostListProps {
//     initialPosts: Post[];
//     users: User[];
//     pageSize?: number;
// }
//
// export function InfinitePostList({
//                                      initialPosts,
//                                      users,
//                                      pageSize = 10
//                                  }: InfinitePostListProps) {
//     // 状态管理
//     const [posts, setPosts] = useState<Post[]>(initialPosts);
//     const [page, setPage] = useState(1);
//     const [loading, setLoading] = useState(false);
//     const [error, setError] = useState<Error | null>(null);
//     const [hasMore, setHasMore] = useState(true);
//
//     // 使用 ref 避免在 observer 回调中使用过期的状态
//     const loadingRef = useRef(false);
//
//     // 获取更多帖子的函数
//     const fetchMorePosts = useCallback(async () => {
//         try {
//             setLoading(true);
//             setError(null);
//             loadingRef.current = true;
//
//             // 模拟 API 调用，实际使用时替换为真实 API
//             const response = await fetch(
//                 `/api/posts?page=${page + 1}&limit=${pageSize}`
//             );
//
//             if (!response.ok) {
//                 throw new Error('Failed to fetch posts');
//             }
//
//             const newPosts = await response.json();
//
//             // 更新状态
//             setPosts(prev => [...prev, ...newPosts]);
//             setPage(prev => prev + 1);
//
//             // 检查是否还有更多数据
//             if (newPosts.length < pageSize) {
//                 setHasMore(false);
//             }
//         } catch (err) {
//             setError(err instanceof Error ? err : new Error('Unknown error occurred'));
//         } finally {
//             setLoading(false);
//             loadingRef.current = false;
//         }
//     }, [page, pageSize]);
//
//     // 设置 Intersection Observer
//     useEffect(() => {
//         // 如果浏览器不支持 Intersection Observer，直接返回
//         if (!('IntersectionObserver' in window)) {
//             console.warn('IntersectionObserver not supported');
//             return;
//         }
//
//         // 创建观察器实例
//         const observer = new IntersectionObserver(
//             (entries) => {
//                 // 如果触发元素进入视口，且不在加载中，且还有更多数据
//                 if (
//                     entries[0].isIntersecting &&
//                     !loadingRef.current &&
//                     hasMore &&
//                     !error
//                 ) {
//                     fetchMorePosts();
//                 }
//             },
//             {
//                 // 设置触发阈值，这里设置为当元素有 10% 进入视口时触发
//                 threshold: 0.1,
//                 // 设置 rootMargin 可以提前触发加载
//                 rootMargin: '100px',
//             }
//         );
//
//         // 获取触发元素
//         const trigger = document.getElementById('scroll-trigger');
//         if (trigger) {
//             observer.observe(trigger);
//         }
//
//         // 清理函数
//         return () => observer.disconnect();
//     }, [fetchMorePosts, hasMore, error]);
//
//     // 获取作者信息的辅助函数
//     const getAuthor = useCallback((authorId: string) => {
//         return users.find(user => user.id === authorId) as User;
//     }, [users]);
//
//     // 处理点赞事件
//     const handleLike = useCallback((postId: string) => {
//         // TODO: 实现点赞逻辑
//         console.log('Like post:', postId);
//     }, []);
//
//     return (
//         <div className="space-y-4">
//             {/* 帖子列表 */}
//             {posts.map((post) => (
//                 <PostCard
//                     key={post.id}
//                     post={post}
//                     author={getAuthor(post.authorId)}
//                     onLike={handleLike}
//                 />
//             ))}
//
//             {/* 加载状态 */}
//             {loading && (
//                 <div className="flex justify-center py-4">
//                     <Loader2 className="w-6 h-6 animate-spin text-blue-500" />
//                 </div>
//             )}
//
//             {/* 错误状态 */}
//             {error && (
//                 <div className="text-center py-4">
//                     <p className="text-red-500 mb-2">{error.message}</p>
//                     <button
//                         onClick={() => fetchMorePosts()}
//                         className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
//                     >
//                         重试
//                     </button>
//                 </div>
//             )}
//
//             {/* 无限滚动触发元素 */}
//             {hasMore && !error && (
//                 <div
//                     id="scroll-trigger"
//                     className="h-10"
//                     aria-hidden="true"
//                 />
//             )}
//
//             {/* 列表末尾提示 */}
//             {!hasMore && !loading && (
//                 <div className="text-center py-4 text-gray-500">
//                     没有更多帖子了
//                 </div>
//             )}
//         </div>
//     );
// }
