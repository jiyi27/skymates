import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { AuthTabs } from "./_components/AuthTabs";

export default function AuthPage() {
    return (
        <Card>
            <CardHeader>
                <CardTitle>Welcome</CardTitle>
                <CardDescription>Please login or create an account to continue</CardDescription>
            </CardHeader>
            <CardContent>
                <AuthTabs />
            </CardContent>
        </Card>
    );
}