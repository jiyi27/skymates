import { SearchBox } from "@/app/search/_components/SearchBox";

export default function Home() {
    return (
        <main className="min-h-screen bg-gray-50 mb-20">
            <div className="container mx-auto px-4 py-8 md:py-12">
                <div className="max-w-2xl mx-auto">
                    <h1 className="text-2xl md:text-3xl font-bold text-center mb-6 md:mb-8">
                        知识点搜索
                    </h1>
                    <SearchBox />
                </div>
            </div>
        </main>
    );
}
