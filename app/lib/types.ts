// 后端通用响应体结构,
// data?: T 表示 data 为可选字段, 比如 json 格式为: {"message": "success"}
export interface ApiResponse<T> {
    message: string;
    data?: T;
}

// 供前端调用者使用的响应体结构, 包含了状态码用于判断请求是否成功, 用户是否登录等状态
export interface Response<T> {
    statusCode: number;
    message: string;
    data?: T;
}

export class ApiError extends Error {
    constructor(
        public statusCode: number,
        message: string,
    ) {
        super(message);
        this.name = 'ApiError';
    }
}

export interface User {
    id: number;
    username: string;
    email: string;
    avatar_url: string;
}

export interface Term {
    id: string;
    name: string;
    explanation: string;
    video_url: string;
    categories: string[];
}

export interface TermSuggestion {
    id: string;
    name: string;
}

export interface Category {
    id: string;
    name: string;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    username: string;
    email: string;
    password: string;
}
