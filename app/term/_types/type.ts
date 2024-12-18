// types.ts
export interface Category {
    id: number;
    name: string;
    description?: string;
}

export interface Term {
    id: number;
    term: string;
    definition: string;
    textExplanation?: string;
    videoUrl?: string;
    // 存储该术语关联的所有类别
    categories: Category[];
    exercises: Exercise[];
}

export interface Exercise {
    id: number;
    type: "multiple_choice" | "short_answer";
    question: string;
    answer: string;
    explanation?: string;
    // 只有在 type 为 multiple_choice 时才有选项
    options?: MultipleChoiceOption[];
}

export interface MultipleChoiceOption {
    id: number;
    optionText: string;
    isCorrect: boolean;
}

// mockData.ts
export const mockCategories: Category[] = [
    {
        id: 1,
        name: "数学",
        description: "基础数学概念",
    },
    {
        id: 2,
        name: "物理",
        description: "物理学基础概念",
    },
];

export const mockTerms: Term[] = [
    {
        id: 1,
        term: "微分",
        definition: "描述函数在某一点的变化率",
        textExplanation: "微分是高等数学中的重要概念...",
        videoUrl: "https://example.com/videos/differential",
        categories: [
            mockCategories[0], // 数学
            mockCategories[1], // 物理
        ],
        exercises: [
            {
                id: 1,
                type: "multiple_choice",
                question: "下列哪项正确描述了微分的概念？",
                answer: "A",
                explanation: "微分描述了函数在某点的瞬时变化率...",
                options: [
                    {
                        id: 1,
                        optionText: "函数在某点的瞬时变化率",
                        isCorrect: true,
                    },
                    {
                        id: 2,
                        optionText: "函数的积分",
                        isCorrect: false,
                    },
                ],
            },
            {
                id: 2,
                type: "multiple_choice",
                question: "下列哪项正确描述了微分的概念？",
                answer: "A",
                explanation: "微分描述了函数在某点的瞬时变化率...",
                options: [
                    {
                        id: 1,
                        optionText: "函数在某点的瞬时变化率",
                        isCorrect: true,
                    },
                    {
                        id: 2,
                        optionText: "函数的积分",
                        isCorrect: false,
                    },
                    {
                        id: 4,
                        optionText: "函数的导数",
                        isCorrect: false,
                    },
                ],
            },
        ],
    },
];

// API 模拟函数
export const mockApi = {
    // 获取所有类别
    getCategories: () => Promise.resolve(mockCategories),

    // 获取某个类别下的所有术语
    getTermsByCategory: (categoryId: number) =>
        Promise.resolve(
            mockTerms.filter((term) =>
                term.categories.some((cat) => cat.id === categoryId)
            )
        ),

    // 获取单个术语的详细信息
    getTerm: (termId: number) =>
        Promise.resolve(mockTerms.find((term) => term.id === termId)),

    // 搜索术语
    searchTerms: (query: string) =>
        Promise.resolve(
            mockTerms.filter((term) =>
                term.term.toLowerCase().includes(query.toLowerCase())
            )
        ),
};
