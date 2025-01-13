export class ApiError extends Error {
    constructor(
        public status: number,
        message: string,
    ) {
        super(message);
        this.name = 'ApiError';
    }
}

export interface Response<T> {
    code: number;
    message: string;
    data: T;
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

