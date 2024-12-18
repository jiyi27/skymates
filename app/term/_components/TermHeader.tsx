// app/term/_components/term-header.tsx
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { type Term } from "@/app/term/_types/type";

interface TermHeaderProps {
    term: Term;
}

export default function TermHeader({ term }: TermHeaderProps) {
    return (
        <div className="space-y-4">
            <h1 className="text-3xl font-bold">{term.term}</h1>
            <div className="flex gap-2">
                {term.categories.map((category) => (
                    <Badge key={category.id} variant="secondary">
                        {category.name}
                    </Badge>
                ))}
            </div>
            <Separator />
        </div>
    );
}
