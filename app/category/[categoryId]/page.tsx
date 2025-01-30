import {TermAPI} from "@/app/lib/api";
import CategoryPageContent from "@/app/category/_components/CategoryPageContent";


export default async function CategoryPage({
   params
}: {
    params: { categoryId: string }
}) {
    const { categoryId } = params;
    const initialTerms = await TermAPI.getTermsByCategoryId(categoryId);


    return (
        <CategoryPageContent
            categoryId={categoryId}
            initialTerms={initialTerms.data?.terms ?? []}
            initialLastId={initialTerms.data?.last_id ?? null}
            initialHasMore={initialTerms.data?.has_more ?? false}
        />
    );
}