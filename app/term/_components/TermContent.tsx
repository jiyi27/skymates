// app/term/_components/term-content.tsx
import { type Term } from "@/app/term/_types/type";

interface TermContentProps {
    term: Term;
}

export default function TermContent({ term }: TermContentProps) {
    return (
        <div className="space-y-6">
            {/* 定义部分 */}
            <section>
                <h2 className="text-xl font-semibold mb-3">定义</h2>
                <p className="text-gray-700">{term.definition}</p>
            </section>

            {/* 详细解释部分 */}
            {term.textExplanation && (
                <section>
                    <h2 className="text-xl font-semibold mb-3">详细解释</h2>
                    <p className="text-gray-700">{term.textExplanation}</p>
                </section>
            )}

            {/* 视频部分 */}
            {term.videoUrl && (
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
