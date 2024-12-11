import Link from "next/link";

export default function Test() {
    return (
        <div>
            <div className="mx-auto px-4 w-full max-w-7xl border-2">
                <div className="flex justify-around items-center">
                    <Link href="/" className="text-blue-600 dark:text-blue-400">首页</Link>
                    <Link href="/discuss" className="text-blue-600 dark:text-blue-400">讨论</Link>
                    <Link href="/search" className="text-blue-600 dark:text-blue-400">搜索</Link>
                </div>
            </div>
        </div>
    );
}