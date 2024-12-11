"use client";

import React, { useState } from 'react';
import { Send, X, Flame, Clock, ThumbsUp } from 'lucide-react';

// 发帖表单组件
interface PostFormProps {
    onSubmit: (title: string, content: string) => void;
    onCancel?: () => void;
}

export const PostForm = ({ onSubmit, onCancel }: PostFormProps) => {
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [isExpanded, setIsExpanded] = useState(false);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (!title.trim() || !content.trim()) return;

        onSubmit(title, content);
        setTitle('');
        setContent('');
        setIsExpanded(false);
    };

    return (
        <form onSubmit={handleSubmit} className="mb-8 bg-white rounded-lg shadow-md p-4">
            {!isExpanded ? (
                <div
                    onClick={() => setIsExpanded(true)}
                    className="p-4 border border-dashed border-gray-300 rounded-lg cursor-text text-gray-500 hover:border-gray-400 transition-colors"
                >
                    点击发布新帖子...
                </div>
            ) : (
                <>
                    <div className="mb-4">
                        <input
                            type="text"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            placeholder="输入标题..."
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            maxLength={100}
                        />
                    </div>
                    <div className="mb-4">
            <textarea
                value={content}
                onChange={(e) => setContent(e.target.value)}
                placeholder="分享你的想法..."
                className="w-full px-4 py-2 border border-gray-300 rounded-lg h-32 resize-none focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                maxLength={2000}
            />
                    </div>
                    <div className="flex justify-end space-x-3">
                        <button
                            type="button"
                            onClick={() => {
                                setIsExpanded(false);
                                setTitle('');
                                setContent('');
                                onCancel?.();
                            }}
                            className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg flex items-center transition-colors"
                        >
                            <X className="w-4 h-4 mr-2" />
                            取消
                        </button>
                        <button
                            type="submit"
                            disabled={!title.trim() || !content.trim()}
                            className="px-4 py-2 bg-blue-500 text-white rounded-lg flex items-center hover:bg-blue-600 transition-colors disabled:bg-gray-300 disabled:cursor-not-allowed"
                        >
                            <Send className="w-4 h-4 mr-2" />
                            发布
                        </button>
                    </div>
                </>
            )}
        </form>
    );
};

// 排序选项类型
export type SortOption = 'latest' | 'hot' | 'mostLiked';

// 排序控制组件
interface SortControlProps {
    currentSort: SortOption;
    onSortChange: (sort: SortOption) => void;
}

export const SortControl = ({ currentSort, onSortChange }: SortControlProps) => {
    const sortOptions = [
        { value: 'latest' as const, label: '最新', icon: Clock },
        { value: 'hot' as const, label: '热门', icon: Flame },
        { value: 'mostLiked' as const, label: '最多点赞', icon: ThumbsUp },
    ];

    return (
        <div className="flex space-x-2 mb-6">
            {sortOptions.map(({ value, label, icon: Icon }) => (
                <button
                    key={value}
                    onClick={() => onSortChange(value)}
                    className={`px-4 py-2 rounded-lg flex items-center transition-colors ${
                        currentSort === value
                            ? 'bg-blue-500 text-white'
                            : 'bg-white text-gray-600 hover:bg-gray-100'
                    }`}
                >
                    <Icon className="w-4 h-4 mr-2" />
                    {label}
                </button>
            ))}
        </div>
    );
};