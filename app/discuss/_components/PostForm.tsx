// app/discuss/_components/PostForm.tsx
"use client";

import React from "react";
import {Card, CardContent} from "@/components/ui/card";
import {Input} from "@/components/ui/input";
import {Textarea} from "@/components/ui/textarea";
import {Button} from "@/components/ui/button";


interface PostFormProps {
    onSubmit: (title: string, content: string) => void;
}

export const PostForm = ({ onSubmit }: PostFormProps) => {
    const [title, setTitle] = React.useState('');
    const [content, setContent] = React.useState('');

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (title.trim() && content.trim()) {
            onSubmit(title, content);
            setTitle('');
            setContent('');
        }
    };

    return (
        <Card className="mb-6">
            <CardContent className="pt-6">
                <form onSubmit={handleSubmit} className="space-y-4">
                    <Input
                        placeholder="标题"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                    />
                    <Textarea
                        placeholder="分享你的想法..."
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                        rows={4}
                    />
                    <Button type="submit" disabled={!title.trim() || !content.trim()}>
                        发布
                    </Button>
                </form>
            </CardContent>
        </Card>
    );
};
