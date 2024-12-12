"use client";

import React from 'react';
import type { SortOption } from './types';
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";

interface SortControlProps {
    currentSort: SortOption;
    onSortChange: (option: SortOption) => void;
}

export const SortControl = ({ currentSort, onSortChange }: SortControlProps) => {
    return (
        <div className="mb-6">
            <Select value={currentSort} onValueChange={onSortChange}>
                <SelectTrigger className="w-[180px]">
                    <SelectValue placeholder="排序方式" />
                </SelectTrigger>
                <SelectContent>
                    <SelectItem value="latest">最新发布</SelectItem>
                    <SelectItem value="hot">最热门</SelectItem>
                    <SelectItem value="mostLiked">最多点赞</SelectItem>
                </SelectContent>
            </Select>
        </div>
    );
};