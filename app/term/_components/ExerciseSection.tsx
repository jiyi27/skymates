// app/term/_components/exercise-section.tsx
"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { useState } from "react";
import { type Exercise } from "@/app/term/_types/type";
import MultipleChoiceExercise from "@/app/term/_components/MultipleChoiceExercise";

interface ExerciseSectionProps {
    exercises: Exercise[];
}

export default function ExerciseSection({ exercises }: ExerciseSectionProps) {
    const [currentIndex, setCurrentIndex] = useState(0)
    // 记录每个题目的提交状态
    const [submittedStates, setSubmittedStates] = useState<boolean[]>(
        new Array(exercises.length).fill(false)
    )
    // 记录每个题目的选择
    const [selections, setSelections] = useState<string[]>(
        new Array(exercises.length).fill("")
    )

    const currentExercise = exercises[currentIndex]

    // 处理答案选择
    const handleSelect = (value: string) => {
        const newSelections = [...selections]
        newSelections[currentIndex] = value
        setSelections(newSelections)
    }

    const handleSubmit = () => {
        const newSubmittedStates = [...submittedStates]
        newSubmittedStates[currentIndex] = true
        setSubmittedStates(newSubmittedStates)
    }

    const handlePrevious = () => {
        setCurrentIndex((prev) => Math.max(0, prev - 1))
    }

    const handleNext = () => {
        setCurrentIndex((prev) => Math.min(exercises.length - 1, prev + 1))
    }

    return (
        <div className="space-y-6">
            <h2 className="text-2xl font-bold">练习题</h2>
            <Card>
                <CardHeader>
                    <CardTitle className="text-lg">
                        第 {currentIndex + 1} 题 / 共 {exercises.length} 题
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    {/* 渲染当前题目 */}
                    <MultipleChoiceExercise
                        exercise={currentExercise}
                        isSubmitted={submittedStates[currentIndex]}
                        selected={selections[currentIndex]}
                        onSelect={handleSelect}
                        onSubmit={handleSubmit}
                    />

                    {/* 导航按钮 */}
                    <div className="flex items-center justify-center gap-8 mt-6">
                        <Button
                            variant="ghost"
                            size="icon"
                            onClick={handlePrevious}
                            disabled={currentIndex === 0}
                        >
                            <ChevronLeft className="h-6 w-6" />
                        </Button>

                        <div className="flex items-center gap-1">
                            {exercises.map((_, index) => (
                                <div
                                    key={index}
                                    className={`h-2 w-2 rounded-full transition-colors ${
                                        index === currentIndex
                                            ? 'bg-primary'
                                            : 'bg-gray-200'
                                    }`}
                                />
                            ))}
                        </div>

                        <Button
                            variant="ghost"
                            size="icon"
                            onClick={handleNext}
                            disabled={currentIndex === exercises.length - 1}
                        >
                            <ChevronRight className="h-6 w-6" />
                        </Button>
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
