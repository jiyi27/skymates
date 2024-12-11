// app/discuss/_data/types.ts
export interface User {
    id: string;
    name: string;
    avatar: string;
}

export interface Post {
    id: string;
    title: string;
    content: string;
    authorId: string;
    createdAt: string;
    likesCount: number;
    commentsCount: number;
    isLiked?: boolean;
}

export interface Comment {
    id: string;
    postId: string;
    authorId: string;
    content: string;
    createdAt: string;
    likesCount: number;
    parentId?: string;
    isLiked?: boolean;
}

// app/discuss/_data/users.ts
export const users: User[] = [
    {
        id: "u1",
        name: "张三",
        avatar: "/avatars/user1.jpg",
    },
    {
        id: "u2",
        name: "李四",
        avatar: "/avatars/user2.jpg",
    },
    {
        id: "u3",
        name: "王五",
        avatar: "/avatars/user3.jpg",
    },
    {
        id: "u4",
        name: "赵六",
        avatar: "/avatars/user4.jpg",
    },
];

// app/discuss/_data/posts.ts
export const posts: Post[] = [
    {
        id: "p1",
        title: "请问有人学习过机器学习吗？想请教一些问题",
        content: "最近在学习机器学习基础，遇到了一些困难。特别是在理解神经网络的反向传播算法时，感觉特别吃力。有经验的同学能分享一下学习方法吗？",
        authorId: "u1",
        createdAt: "2024-12-09T15:30:00Z",
        likesCount: 42,
        commentsCount: 15,
        isLiked: false,
    },
    {
        id: "p2",
        title: "分享一个学习编程的小技巧",
        content: "在学习新的编程概念时，我发现用思维导图来整理知识点特别有用。这里分享一下我整理的 JavaScript 异步编程的思维导图...",
        authorId: "u2",
        createdAt: "2024-12-10T09:15:00Z",
        likesCount: 89,
        commentsCount: 23,
        isLiked: true,
    },
    {
        id: "p3",
        title: "推荐一个超棒的算法可视化网站",
        content: "今天发现了一个很棒的算法可视化网站，对理解排序算法特别有帮助。网站通过动画的方式展示了各种排序算法的执行过程...",
        authorId: "u3",
        createdAt: "2023-12-10T11:45:00Z",
        likesCount: 156,
        commentsCount: 34,
        isLiked: false,
    },
    {
        id: "p4",
        title: "React Hooks 使用心得",
        content: "使用 React Hooks 已经有两年了，想分享一下我在实际项目中积累的一些经验和踩过的坑...",
        authorId: "u4",
        createdAt: "2024-12-10T13:20:00Z",
        likesCount: 267,
        commentsCount: 45,
        isLiked: false,
    },
];

// app/discuss/_data/comments.ts
export const comments: Comment[] = [
    {
        id: "c1",
        postId: "p1",
        authorId: "u2",
        content: "建议先从感知机开始学习，理解基本原理后再深入神经网络",
        createdAt: "2024-12-09T16:00:00Z",
        likesCount: 12,
        isLiked: false,
    },
    {
        id: "c2",
        postId: "p1",
        authorId: "u3",
        content: "推荐你看看 3Blue1Brown 的神经网络视频系列，讲解非常直观",
        createdAt: "2024-12-09T16:15:00Z",
        likesCount: 18,
        isLiked: true,
    },
    {
        id: "c3",
        postId: "p1",
        parentId: "c2",
        authorId: "u1",
        content: "谢谢推荐！我这就去看看",
        createdAt: "2024-12-09T16:30:00Z",
        likesCount: 5,
        isLiked: false,
    },
];