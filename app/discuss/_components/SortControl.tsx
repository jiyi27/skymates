"use client";

import React from 'react';
import { Clock, Flame, ThumbsUp } from 'lucide-react';
import type { SortOption } from './types';

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