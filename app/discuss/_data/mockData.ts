// app/discuss/_data/types.ts
export type User = {
    id: string;
    username: string;
    name: string;
    avatarUrl: string | null;
    email: string;
    createdAt: string;
}

export type Comment = {
    id: string;
    postId: string;
    userId: string;
    content: string;
    likesCount: number;
    replyToId: string | null;
    replyTo: {
        id: string;
        content: string;
        userId: string;
        username: string;
    } | null;
    user: User;
    isLiked: boolean;
    createdAt: string;
    updatedAt: string;
}

export type Post = {
    id: string;
    authorId: string;
    title: string;
    content: string;
    likesCount: number;
    commentsCount: number;
    user: User;
    isLiked: boolean;
    comments: Comment[];
    createdAt: string;
    updatedAt: string;
}

const users: User[] = [
    {
        id: '1',
        username: 'TechGuru',
        name: '张三',
        email: 'tech.guru@example.com',
        avatarUrl: '/avatars/1.png',
        createdAt: '2024-01-01T00:00:00Z',
    },
    {
        id: '2',
        username: 'CodeMaster',
        name: '赵四',
        email: 'code.master@example.com',
        avatarUrl: '/avatars/2.png',
        createdAt: '2024-01-01T00:00:00Z',
    },
    {
        id: '3',
        username: 'DesignPro',
        name: '王五',
        email: 'design.pro@example.com',
        avatarUrl: '/avatars/3.png',
        createdAt: '2024-01-01T00:00:00Z',
    },
    {
        id: '4',
        username: 'DataNinja',
        name: '李六',
        email: 'data.ninja@example.com',
        avatarUrl: '/avatars/4.png',
        createdAt: '2024-01-01T00:00:00Z',
    },
];

const comments: Comment[] = [
    {
        id: '1',
        postId: '1',
        userId: '2',
        content: '这个观点非常好，特别是关于性能优化的部分！',
        likesCount: 3,
        replyToId: null,
        replyTo: null,
        user: users[1],
        isLiked: true,
        createdAt: '2024-01-01T01:00:00Z',
        updatedAt: '2024-01-01T01:00:00Z',
    },
    {
        id: '2',
        postId: '1',
        userId: '3',
        content: '完全同意你的看法，我在项目中也遇到类似的问题。',
        likesCount: 2,
        replyToId: '1',
        replyTo: {
            id: '1',
            content: '这个观点非常好，特别是关于性能优化的部分！',
            userId: '2',
            username: 'CodeMaster',
        },
        user: users[2],
        isLiked: false,
        createdAt: '2024-01-01T02:00:00Z',
        updatedAt: '2024-01-01T02:00:00Z',
    },
    {
        id: '3',
        postId: '1',
        userId: '1',
        content: '感谢支持！我后续会分享更多相关内容。',
        likesCount: 1,
        replyToId: '2',
        replyTo: {
            id: '2',
            content: '完全同意你的看法，我在项目中也遇到类似的问题。',
            userId: '3',
            username: 'DesignPro',
        },
        user: users[0],
        isLiked: false,
        createdAt: '2024-01-01T03:00:00Z',
        updatedAt: '2024-01-01T03:00:00Z',
    },
];

const posts: Post[] = [
    {
        id: '1',
        authorId: '1',
        title: 'React性能优化最佳实践',
        content: '在开发大型React应用时，性能优化是一个非常重要的话题。本文将分享一些实用的优化技巧...',
        likesCount: 15,
        commentsCount: 3,
        user: users[0],
        isLiked: true,
        comments: comments,
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
    },
    {
        id: '2',
        authorId: '2',
        title: 'TypeScript高级特性详解',
        content: 'TypeScript提供了许多强大的类型系统特性，今天我们来深入了解一下...',
        likesCount: 8,
        commentsCount: 0,
        user: users[1],
        isLiked: false,
        comments: [],
        createdAt: '2024-01-02T00:00:00Z',
        updatedAt: '2024-01-02T00:00:00Z',
    },
    {
        id: '3',
        authorId: '3',
        title: 'CSS Grid布局完全指南',
        content: 'CSS Grid是一个强大的布局工具，本文将详细介绍其使用方法和实践技巧...',
        likesCount: 12,
        commentsCount: 0,
        user: users[2],
        isLiked: true,
        comments: [],
        createdAt: '2024-01-03T00:00:00Z',
        updatedAt: '2024-01-03T00:00:00Z',
    },
    {
        id: '4',
        authorId: '4',
        title: '前端测试策略',
        content: '一个完善的测试策略对于保证代码质量至关重要，让我们来看看前端测试的几种方法...',
        likesCount: 6,
        commentsCount: 0,
        user: users[3],
        isLiked: false,
        comments: [],
        createdAt: '2024-01-04T00:00:00Z',
        updatedAt: '2024-01-04T00:00:00Z',
    },
    {
        id: '5',
        authorId: '1',
        title: 'Next.js 13新特性解析',
        content: 'Next.js 13带来了许多令人兴奋的新特性，让我们一起来看看这些变化...',
        likesCount: 10,
        commentsCount: 0,
        user: users[0],
        isLiked: true,
        comments: [],
        createdAt: '2024-01-05T00:00:00Z',
        updatedAt: '2024-01-05T00:00:00Z',
    },
];

export { users, comments, posts };