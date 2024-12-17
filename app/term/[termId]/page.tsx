import { posts } from "@/app/discuss/_data/mockData";
import { notFound } from "next/navigation";

export default async function Page({
    params,
}: {
    params: Promise<{ postId: string }>;
}) {
    const { postId } = await params;
    const post = posts.find((p) => p.id === postId);

    if (!post) {
        notFound();
    }
}
