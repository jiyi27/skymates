// app/hooks/useDebounce.ts
'use client';

import {useEffect, useState} from "react";

export function useDebounce<T>(value: T, delay: number): T {
    const [debouncedValue, setDebouncedValue] = useState(value);

    useEffect(() => {
        const timer = setTimeout(() => {
            setDebouncedValue(value);
        }, delay);

        // 清理函数, 在下一次 effect 运行之前运行, 避免内存泄漏
        return () => {
            clearTimeout(timer);
        };
    }, [value, delay]);

    return debouncedValue;
}