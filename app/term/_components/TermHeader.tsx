// app/term/_components/term-header.tsx
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { Term } from "@/app/lib/types";
import Link from "next/link";

interface TermHeaderProps {
    term: Term;
}

export default function TermHeader({ term }: TermHeaderProps) {
    return (
        <div className="space-y-4">
            <h1 className="text-3xl font-bold">{term.name}</h1>
            <div className="flex gap-2">
                {term.categories.map((category) => (
                    <Link href={`/category/${category.id}`} key={category.id}>
                        <Badge key={category.id} variant="secondary">
                            {category.name}
                        </Badge>
                    </Link>
                ))}
            </div>
            <Separator />
        </div>
    );
}
