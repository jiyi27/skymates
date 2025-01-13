import { useState } from 'react';
import {TermSuggestion} from "@/app/lib/types";
import {TermAPI} from "@/app/lib/api";

export function useSearch() {
    const [searchResults, setSearchResults] = useState<TermSuggestion[]>([]);

    const handleSearch = async (query: string) => {
        if (query.trim()) {
            try {
                const results = await TermAPI.getSuggestions(query);
                setSearchResults(results.data ?? []);
            } catch (error) {
                console.error('Search failed:', error);
                setSearchResults([]);
            }
        }
    };

    return { searchResults, handleSearch };
}
