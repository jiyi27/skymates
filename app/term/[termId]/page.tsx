import TermContent from "@/app/term/_components/TermContent";
import TermHeader from "@/app/term/_components/TermHeader";
import {notFound} from "next/navigation";
import {TermAPI} from "@/app/lib/api";
import {Term} from "@/app/lib/types";

export default async function TermDetailPage({
    params,
}: {
    params: Promise<{ termId: string }>;
}) {
    const { termId } = await params;
    let term: Term | null = null;
    try {
        const response = await TermAPI.getTermById(termId);
        term = response.data ?? null;
    } catch (error) {
        console.error('TermDetailPage.getTerm() error:', error);
    }

    if (!term) {
        notFound();
    }

    return (
        <div className="max-w-4xl mx-auto p-4 space-y-8 mb-20">
            <TermHeader term={term} />
            <TermContent term={term} />
            {/*<ExerciseSection exercises={term.exercises} />*/}
        </div>
    );
}
