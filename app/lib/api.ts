import {ApiError, Response, Term, TermSuggestion, User} from "@/app/lib/types";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:3000/api';

const defaultHeaders = {
    'Content-Type': 'application/json',
};

export async function fetchApi<T>(
    endpoint: string,
    options: RequestInit = {}
): Promise<T> {
    // 最好不要直接修改 options 对象, 而是创建一个新的对象
    // 这样可以避免副作用, 使函数更容易测试和调试
    const mergedOptions: RequestInit = {
        ...options,
        headers: {
            ...defaultHeaders,
            ...options.headers,
        },
    };

    const response = await fetch(`${API_BASE_URL}${endpoint}`, mergedOptions);
    const data = await response.json();

    if (!response.ok) {
        throw new ApiError(
            response.status,
            data.message || 'Something went wrong'
        );
    }

    return data;
}

/**
 * 用户相关 API
 */

interface LoginRequest {
    email: string;
    password: string;
}

interface RegisterRequest {
    username: string;
    email: string;
    password: string;
}

export const UserAPI = {
    login: (data: LoginRequest) =>
        fetchApi<Response<{ token: string; user: User }>>('/auth/login', {
            method: 'POST',
            body: JSON.stringify(data),
        }),

    register: (data: RegisterRequest) =>
        fetchApi<Response<{ token: string; user: User }>>('/auth/register', {
            method: 'POST',
            body: JSON.stringify(data),
        }),

    logout: () =>
        fetchApi<Response<void>>('/auth/logout', {
            method: 'POST',
        }),

    // getUserProfile: (userId: number) =>
    //     fetchApi<Response<User>>(`/users/${userId}`),

    // updateProfile: (data: UpdateProfileRequest) =>
    //     fetchApi<Response<User>>('/users/profile', {
    //         method: 'PUT',
    //         body: JSON.stringify(data),
    //     }),

    // 修改密码
    changePassword: (oldPassword: string, newPassword: string) =>
        fetchApi<Response<void>>('/users/password', {
            method: 'PUT',
            body: JSON.stringify({ oldPassword, newPassword }),
        }),
};

/**
 * 术语相关 API
 */
export const TermAPI = {
    getTerm: (id: string) =>
        fetchApi<Response<Term>>(`/terms/${id}`),

    getSuggestions: (query: string) =>
        fetchApi<Response<TermSuggestion[]>>(`/terms/suggestions?query=${encodeURIComponent(query)}`),

    getTerms: (category: string) =>
        fetchApi<Response<Term[]>>(`/terms?category=${encodeURIComponent(category)}`),
};