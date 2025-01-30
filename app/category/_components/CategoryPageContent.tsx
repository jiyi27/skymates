'use client';

import Link from "next/link";
import {Term} from "@/app/lib/types";
import {useCallback, useEffect, useState} from "react";
import {useInView} from "react-intersection-observer";
import {TermAPI} from "@/app/lib/api";

interface CategoryClientProps {
    categoryId: string;
    initialTerms: Term[];
    initialLastId: string | null;
    initialHasMore: boolean;
}

export default function CategoryPageContent({
   categoryId,
   initialTerms,
   initialLastId,
   initialHasMore
}: CategoryClientProps) {
    const [terms, setTerms] = useState<Term[]>(initialTerms);
    const [lastId, setLastId] = useState<string | null>(initialLastId);
    const [hasMore, setHasMore] = useState(initialHasMore);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const {ref, inView} = useInView();

    const fetchTerms = useCallback(async (lastId?: string) => {
        try {
            setLoading(true);
            const response = await TermAPI.getTermsByCategoryId(categoryId, {lastId: lastId});
            setTerms(prev => [...prev, ...response.data?.terms ?? []]);
            setLastId(response.data?.last_id ?? null);
            setHasMore(response.data?.has_more ?? false);
        } catch (error) {
            console.error('CategoryPage.fetchTerms() error:', error);
            setError('Failed to load terms');
        } finally {
            setLoading(false);
        }
    }, [categoryId]);

    useEffect(() => {
        if (inView && hasMore && !loading) {
            fetchTerms(lastId ?? '').then();
        }
    }, [inView, hasMore, loading, fetchTerms, lastId]);

    if (error) {
        return <div className="text-center text-red-500">{error}</div>;
    }

    return (
        <div>
            <div className="max-w-4xl mx-auto p-4">
                {/*{terms.length > 0 && (*/}
                {/*    <h1 className="text-2xl font-bold mb-4">*/}
                {/*        {terms[0].categories[0].name}*/}
                {/*    </h1>*/}
                {/*)}*/}

                <div className="space-y-4">
                    {terms.map((term) => (
                        <Link
                            href={`/term/${term.id}`}
                            key={term.id}
                        >
                            <div className="p-4 bg-white rounded-lg shadow-sm border hover:shadow-md transition-shadow">
                                <h3 className="text-lg font-medium text-gray-900">
                                    {term.name}
                                </h3>
                            </div>
                        </Link>
                    ))}
                </div>

                {/* 加载更多触发器 */}
                {hasMore && (
                    <div ref={ref} className="py-4 text-center">
                        {loading ? (
                            <div>Loading...</div>
                        ) : (
                            <div>Scroll to load more</div>
                        )}
                    </div>
                )}

                {!hasMore && terms.length > 0 && (
                    <div className="py-4 text-center text-gray-500">
                        No more terms to load
                    </div>
                )}

                {!hasMore && terms.length === 0 && (
                    <div className="py-4 text-center">
                        No terms found in this category
                    </div>
                )}
            </div>
        </div>
    );
}