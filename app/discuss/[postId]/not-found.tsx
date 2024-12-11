import Link from "next/link";


export default function NotFound() {
    return (
        <div className="max-w-4xl mx-auto p-4 text-center">
            <h1 className="text-2xl font-bold mb-4">404 - 帖子未找到</h1>
            <p>抱歉，您要查看的帖子不存在或已被删除。</p>
            <Link href="/discuss" className="text-blue-500 hover:underline mt-4 inline-block">
                返回讨论区
            </Link>
        </div>
    );
}