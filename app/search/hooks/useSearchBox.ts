import React, { useState, useRef } from 'react';
import { useDebounce } from './useDebounce';
import { useClickOutside } from './useClickOutside';
import { useSuggestions } from './useSuggestions';
import { useSearch } from './useSearch';

export function useSearchBox() {
    const [query, setQuery] = useState('');
    const [showSuggestions, setShowSuggestions] = useState(false);
    const wrapperRef = useRef<HTMLDivElement>(null);

    // 1. 用户输入, 组件重新渲染, useDebounce() 被调用
    // 2. 若 500ms 内用户输入abc, 则 useDebounce() 会被调用3次
    // 3. useDebounce() 会在每次调用时清除上一次的定时器, 因为 useEffect() 返回的清理函数会在下一次 effect 运行之前运行
    const debouncedQuery = useDebounce(query, 500);
    const suggestions = useSuggestions(debouncedQuery);
    const { searchResults, handleSearch } = useSearch();

    useClickOutside(wrapperRef, () => setShowSuggestions(false));

    const handleSearchAndCloseSuggestions = async (searchQuery: string) => {
        const success = await handleSearch(searchQuery);
        if (success) {
            setShowSuggestions(false);
        }
    };

    // 处理建议项点击的函数
    const handleSuggestionClick = async (term: string) => {
        setQuery(term);
        await handleSearchAndCloseSuggestions(term);
    };

    const handleKeyPress = async (e: React.KeyboardEvent) => {
        if (e.key === 'Enter') {
            const success = await handleSearch(query);
            if (success) {
                setShowSuggestions(false);
            }
        }
    };

    return {
        query,
        setQuery,
        suggestions,
        searchResults,
        showSuggestions,
        setShowSuggestions,
        handleSearch: () => handleSearchAndCloseSuggestions(query),
        handleSuggestionClick,
        handleKeyPress,
        wrapperRef
    };
}