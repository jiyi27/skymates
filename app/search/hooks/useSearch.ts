import { useState } from 'react';
import { Term, searchTerms } from '@/app/search/_data/mockData';

export function useSearch() {
    const [searchResults, setSearchResults] = useState<Term[]>([]);

    const handleSearch = async (query: string) => {
        if (query.trim()) {
            try {
                const results = await searchTerms(query);
                setSearchResults(results);
                return true;
            } catch (error) {
                console.error('Search failed:', error);
                setSearchResults([]);
                return false;
            }
        }
        return false;
    };

    return { searchResults, handleSearch };
}
