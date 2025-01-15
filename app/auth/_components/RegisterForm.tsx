'use client';

import React, { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { UserAPI } from "@/app/lib/api";
import type { RegisterRequest } from "@/app/lib/types";

export const RegisterForm = () => {
    const [error, setError] = useState<string>("");
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState<string>("");

    const onSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError("");
        setLoading(false);

        const formData = new FormData(e.currentTarget);
        const data: RegisterRequest = {
            username: formData.get("username") as string,
            email: formData.get("email") as string,
            password: formData.get("password") as string,
        };

        try {
            await UserAPI.register(data);
            setSuccess("Registration successful!");
        } catch (err: unknown) {
            if (err instanceof Error) {
                setError("Registration failed: " + err.message);
            } else {
                setError("An error occurred during registration");
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

            {success && (
                <Alert className="border-green-500 text-green-700 bg-green-50">
                    <AlertDescription>{success}</AlertDescription>
                </Alert>
            )}

            <div className="space-y-2">
                <Label htmlFor="username">Username</Label>
                <Input
                    id="username"
                    name="username"
                    type="text"
                    required
                    placeholder="Choose a username"
                />
            </div>

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
                    placeholder="Choose a password"
                />
            </div>

            <Button type="submit" className="w-full" disabled={loading}>
                {loading ? "Creating account..." : "Register"}
            </Button>
        </form>
    );
};

// 其他组件保持不变...