import { useState, useEffect } from 'react';
import { Term, searchTerms } from '@/app/search/_data/mockData';

export function useSuggestions(debouncedQuery: string) {
    const [suggestions, setSuggestions] = useState<Term[]>([]);

    useEffect(() => {
        let isMounted = true;

        async function updateSuggestions() {
            if (!debouncedQuery) {
                setSuggestions([]);
                return;
            }

            try {
                const results = await searchTerms(debouncedQuery);
                if (isMounted) {
                    setSuggestions(results);
                }
            } catch (error) {
                console.error('Failed to fetch suggestions:', error);
                if (isMounted) {
                    setSuggestions([]);
                }
            }
        }

        updateSuggestions().then();

        return () => {
            isMounted = false;
        };
    }, [debouncedQuery]);

    return suggestions;
}