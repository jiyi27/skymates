import { mockApi } from "@/app/term/_types/type";
import ExerciseSection from "@/app/term/_components/ExerciseSection";
import TermContent from "@/app/term/_components/TermContent";
import TermHeader from "@/app/term/_components/TermHeader";

export default async function Page({
    params,
}: {
    params: Promise<{ termId: string }>;
}) {
    const { termId } = await params;
    const term = await mockApi.getTerm(parseInt(termId));

    if (!term) {
        notFound();
    }

    if (!term) {
        return <div className="p-4">术语不存在</div>;
    }

    return (
        <div className="max-w-4xl mx-auto p-4 space-y-8 pb-20">
            <TermHeader term={term} />
            <TermContent term={term} />
            <ExerciseSection exercises={term.exercises} />
        </div>
    );
}
function notFound() {
    throw new Error("Function not implemented.");
}
