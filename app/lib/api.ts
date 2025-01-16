import {
    Response,
    Term,
    TermSuggestion,
    User,
    LoginRequest,
    RegisterRequest,
    ApiResponse,
    ApiError
} from "@/app/lib/types";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api';

export async function fetchApi<T>(
    endpoint: string,
    options: RequestInit = {}
): Promise<Response<T>> {
    // as const 将对象的所有属性设为只读
    const defaultHeaders = {
        'Content-Type': 'application/json',
    } as const;

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
    // 使用类型断言(type assertion), 更好的进行类型推断
    // 如果响应体不合法(比如响应体为空, 或者不符合类型断言), 这里会抛出异常
    const apiResponse = await response.json() as ApiResponse<T>;

    // response.ok 为 true 表示状态码在 200-299 之间
    if (!response.ok) {
        throw new ApiError(response.status, apiResponse.message);
    }

    return {
        ...apiResponse,
        statusCode: response.status,
    }
}

/**
 * 用户相关 API
 */

export const UserAPI = {
    login: (data: LoginRequest) =>
        fetchApi<{ token: string; user: User }>('/auth/login', {
            method: 'POST',
            body: JSON.stringify(data),
        }),

    register: (data: RegisterRequest) =>
        fetchApi<void>('/auth/register', {
            method: 'POST',
            body: JSON.stringify(data),
        }),

    logout: () =>
        fetchApi<void>('/auth/logout', {
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
        fetchApi<void>('/users/password', {
            method: 'PUT',
            body: JSON.stringify({ oldPassword, newPassword }),
        }),
};

/**
 * 术语相关 API
 */
export const TermAPI = {
    getTerm: (id: string) =>
        fetchApi<Term>(`/terms/${id}`),

    getSuggestions: (query: string) =>
        fetchApi<TermSuggestion[]>(`/terms/suggestions?query=${encodeURIComponent(query)}`),

    getTerms: (category: string) =>
        fetchApi<Term[]>(`/terms?category=${encodeURIComponent(category)}`),
};