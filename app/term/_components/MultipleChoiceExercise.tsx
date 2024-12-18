// app/term/_components/multiple-choice-exercise.tsx

"use client";

import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Label } from "@/components/ui/label";
import { type Exercise } from "@/app/term/_types/type";
import { Button } from "@/components/ui/button";

interface MultipleChoiceExerciseProps {
    exercise: Exercise
    isSubmitted: boolean
    selected: string
    onSelect: (value: string) => void
    onSubmit: () => void
}

export default function MultipleChoiceExercise({
                                                   exercise,
                                                   isSubmitted,
                                                   selected,
                                                   onSelect,
                                                   onSubmit
                                               }: MultipleChoiceExerciseProps) {
    // 找到正确选项
    const correctOption = exercise.options?.find(opt => opt.isCorrect)
    // 判断答案是否正确
    const isCorrect = selected === correctOption?.id.toString()

    return (
        <div className="space-y-4">
            <p className="font-medium">{exercise.question}</p>

            <RadioGroup value={selected} onValueChange={onSelect}>
                {exercise.options?.map(option => (
                    <div key={option.id} className="flex items-center space-x-2 py-2">
                        <RadioGroupItem
                            value={option.id.toString()}
                            disabled={isSubmitted}
                        />
                        <Label>
                            {option.optionText}
                            {isSubmitted && (
                                <>
                                    {/* 如果是用户选的选项，显示对错 */}
                                    {selected === option.id.toString() && (
                                        <span className={isCorrect ? "text-green-600 ml-2" : "text-red-600 ml-2"}>
                      {isCorrect ? "✓ 正确" : "✗ 错误"}
                    </span>
                                    )}
                                    {/* 如果用户选错了，显示正确答案 */}
                                    {!isCorrect && option.isCorrect && (
                                        <span className="text-green-600 ml-2">← 正确答案</span>
                                    )}
                                </>
                            )}
                        </Label>
                    </div>
                ))}
            </RadioGroup>

            {!isSubmitted && (
                <Button
                    onClick={onSubmit}
                    disabled={!selected}
                >
                    提交答案
                </Button>
            )}
        </div>
    )
}