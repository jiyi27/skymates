import { RefObject, useEffect } from 'react';

// 点击外部关闭建议列表的处理函数, 依赖数组为空，事件监听器只被添加和移除一次, 不会影响性能
// contains() 是 DOM API，用于检查一个节点是否是另一个节点的后代
// event.target 是触发点击事件的具体元素
// 因为 contains 方法需要 Node 类型参数，所以需要进行类型断言, 否则会报错:
// Argument of type EventTarget | null is not assignable to parameter of type Node | null
export function useClickOutside(ref: RefObject<HTMLElement | null>, callback: () => void) {
    useEffect(() => {
        const handler = (event: MouseEvent) => {
            if (ref.current && !ref.current.contains(event.target as Node)) {
                callback();
            }
        };

        document.addEventListener('mousedown', handler);
        return () => document.removeEventListener('mousedown', handler);
    }, [ref, callback]);
}