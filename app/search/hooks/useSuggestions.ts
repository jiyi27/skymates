import { useState, useEffect } from 'react';
import {TermAPI} from "@/app/lib/api";
import {TermSuggestion} from "@/app/lib/types";

export function useSuggestions(debouncedQuery: string) {
    const [suggestions, setSuggestions] = useState<TermSuggestion[]>([]);

    useEffect(() => {
        // 判断组件是否被卸载(组件完全从 DOM 树中被移除), 避免在组件卸载后更新状态
        // 如果没有 isMounted 检查, 请求返回时仍会尝试更新一个已经不存在的组件的状态
        // 这就会导致 React 警告: Can't perform a React state update on an unmounted component
        let isMounted = true;

        async function updateSuggestions() {
            // 输入为空, 清空建议列表
            if (!debouncedQuery) {
                setSuggestions([]);
                return;
            }

            try {
                const results = await TermAPI.getSuggestions(debouncedQuery);
                if (isMounted) {
                    setSuggestions(results.data ?? []);
                }
            } catch (error) {
                console.error('useSuggestions() error:', error);
                if (isMounted) {
                    setSuggestions([]);
                }
            }
        }

        updateSuggestions().then();

        // 清理函数, commit 阶段执行
        return () => {
            isMounted = false;
        };
    }, [debouncedQuery]);

    return suggestions;
}