// app/term/_components/term-content.tsx
import { Term } from "@/app/lib/types";

interface TermContentProps {
    term: Term;
}

export default function TermContent({ term }: TermContentProps) {
    return (
        <div className="space-y-6">
            {term.explanation && (
                <section>
                    <p className="text-gray-700">{term.explanation}</p>
                </section>
            )}
            {term.video_url && (
                <section>
                    <h2 className="text-xl font-semibold mb-3">视频讲解</h2>
                    <div className="aspect-video bg-gray-100 rounded-lg flex items-center justify-center">
                        {/* 这里可以替换成真实的视频播放器组件 */}
                        <span className="text-gray-500">视频播放区域</span>
                    </div>
                </section>
            )}
        </div>
    );
}
