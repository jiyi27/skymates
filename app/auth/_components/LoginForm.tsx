'use client';

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { UserAPI } from "@/app/lib/api";
import {ApiError, LoginRequest} from "@/app/lib/types";

export const LoginForm = () => {
    const router = useRouter();
    const [error, setError] = useState<string>("");
    const [loading, setLoading] = useState(false);

    const onSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError("");
        setLoading(true);

        const formData = new FormData(e.currentTarget);
        const data: LoginRequest = {
            email: formData.get("email") as string,
            password: formData.get("password") as string,
        };

        try {
            const response = await UserAPI.login(data);

            if (response.data?.token) {
                localStorage.setItem("token", response.data.token);
                localStorage.setItem("user", JSON.stringify(response.data.user));
                router.push("/");
            } else {
                setError("Login failed: " + response.message + ", status: " + response.statusCode);
            }
        } catch (err) {
            if (err instanceof ApiError) {
                setError("Login failed: " + err.message + ", status: " + err.statusCode);
            } else if (err instanceof Error) {
                setError("Login failed: " + err.message);
            } else {
                setError("An unknown error occurred during login");
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <form onSubmit={onSubmit} className="space-y-4 mt-4">
            {error && (
                <Alert variant="destructive">
                    <AlertDescription>{error}</AlertDescription>
                </Alert>
            )}

            <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                <Input
                    id="email"
                    name="email"
                    type="email"
                    required
                    placeholder="Enter your email"
                />
            </div>

            <div className="space-y-2">
                <Label htmlFor="password">Password</Label>
                <Input
                    id="password"
                    name="password"
                    type="password"
                    required
                    placeholder="Enter your password"
                />
            </div>

            <Button type="submit" className="w-full" disabled={loading}>
                {loading ? "Logging in..." : "Login"}
            </Button>
        </form>
    );
};