// app/components/search/search-box.tsx
'use client';

import { Search } from 'lucide-react';
import {useSearchBox} from "@/app/search/hooks/useSearchBox";

export function SearchBox() {
    const {
        query,
        setQuery,
        suggestions,
        searchResults,
        showSuggestions,
        setShowSuggestions,
        handleSuggestionClick,
        handleSearch,
        handleKeyPress,
        wrapperRef
    } = useSearchBox();

    return (
        <div className="w-full">
            <div ref={wrapperRef} className="relative">
                <div className="relative">
                    {/* 搜索输入框 */}
                    <input
                        type="text"
                        value={query}
                        onChange={(e) => setQuery(e.target.value)}
                        onFocus={() => setShowSuggestions(true)}
                        onKeyDown={handleKeyPress}
                        className="w-full px-4 py-3 pr-12 text-base md:text-lg border rounded-lg
                     focus:outline-none focus:ring-2 focus:ring-blue-500
                     shadow-sm transition-all"
                        placeholder="输入关键字搜索..."
                    />
                    {/* 搜索按钮 */}
                    <button
                        onClick={handleSearch}
                        className="absolute right-2 top-1/2 transform -translate-y-1/2
                     p-2 text-gray-400 hover:text-blue-500 rounded-full
                     hover:bg-blue-50 transition-all duration-200"
                        aria-label="搜索"
                    >
                        <Search size={20} />
                    </button>
                </div>

                {/* 搜索建议下拉列表 */}
                {showSuggestions && suggestions.length > 0 && (
                    <div className="absolute z-50 w-full mt-1 bg-white rounded-lg shadow-lg
                        border max-h-[300px] overflow-y-auto">
                        {suggestions.map((term) => (
                            <div
                                key={term.id}
                                className="p-3 hover:bg-gray-50 cursor-pointer border-b last:border-b-0
                         transition-colors"
                                onClick={() => handleSuggestionClick(term.name)}
                            >
                                <div className="font-medium text-sm md:text-base">{term.name}</div>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* 搜索结果显示区域 */}
            {searchResults.length > 0 && (
                <div className="mt-8">
                    <h2 className="text-xl font-semibold mb-4">搜索结果</h2>
                    <div className="grid gap-4">
                        {searchResults.map((result) => (
                            <div
                                key={result.id}
                                className="p-4 bg-white rounded-lg shadow-sm border hover:shadow-md
                         transition-shadow"
                            >
                                <h3 className="text-lg font-medium text-gray-900">
                                    {result.name}
                                </h3>
                                <p className="mt-2 text-gray-600">
                                    {result.description}
                                </p>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* 无搜索结果时的提示 */}
            {query && searchResults.length === 0 && (
                <div className="mt-8 text-center text-gray-500">
                    未找到相关结果
                </div>
            )}
        </div>
    );
}