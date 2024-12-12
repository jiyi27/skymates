// import Link from "next/link";

'use client';

import React, { useState, memo } from 'react';

const NormalChild = ({ name }: { name: string }) => {
    console.log('NormalChild is rendering');

    return (
        <div className="p-4 border rounded bg-gray-100">
            <p>This is a normal component, name: {name}</p>
        </div>
    );
};

const Child = ({ name }: { name: string }) => {
    console.log('MemoChild is rendering');

    return (
        <div className="p-4 border rounded bg-gray-100">
            <p>This is a memo component, name: {name}</p>
        </div>
    );
};

// Use React.memo to wrap the component, return a memoized version of the component
const MemoChild = memo(Child);

export default function ParentPage() {
    const [count, setCount] = useState(0);
    const [name, setName] = useState('David');

    return (
        <div className="p-4 space-y-4">
            <div className="space-x-2">
                <button
                    onClick={() => setCount(c => c + 1)}
                    className="px-4 py-2 bg-blue-500 text-white rounded"
                >
                    Click to update count: {count}
                </button>
                <button
                    onClick={() => setName(name === 'David' ? 'Jack' : 'David')}
                    className="px-4 py-2 bg-green-500 text-white rounded"
                >
                    Click to update name: {name}
                </button>
            </div>

            <div className="space-y-4">
                <NormalChild name={name}/>
                <MemoChild name={name}/>
            </div>
        </div>
    );
};

// export default function Test() {
//     return (
//         <div>
//             <div className="mx-auto px-4 w-full max-w-7xl border-2">
//                 <div className="flex justify-around items-center">
//                     <Link href="/" className="text-blue-600 dark:text-blue-400">首页</Link>
//                     <Link href="/discuss" className="text-blue-600 dark:text-blue-400">讨论</Link>
//                     <Link href="/search" className="text-blue-600 dark:text-blue-400">搜索</Link>
//                 </div>
//             </div>
//         </div>
//     );
// }