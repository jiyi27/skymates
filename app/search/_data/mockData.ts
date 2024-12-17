
export interface Term {
    id: number;
    name: string;
    description: string;
}

// 模拟数据
export const mockTerms: Term[] = [
    { id: 1, name: "数据结构", description: "计算机科学中的基础概念，研究数据的组织方式" },
    { id: 2, name: "数据库", description: "用于存储和管理数据的系统" },
    { id: 3, name: "数据分析", description: "通过检查数据来得出结论的过程" },
    { id: 4, name: "算法", description: "解决问题的步骤和方法" },
    { id: 5, name: "算法复杂度", description: "衡量算法执行效率的指标" },
    { id: 6, name: "网络协议", description: "计算机网络中数据通信的规则" },
    { id: 7, name: "编程语言", description: "用于编写计算机程序的形式语言" },
    { id: 8, name: "操作系统", description: "管理计算机硬件与软件资源的系统软件" }
];

// 模拟API调用
export async function searchTerms(query: string): Promise<Term[]> {
    // 模拟网络请求
    return mockTerms.filter(term =>
        term.name.toLowerCase().startsWith(query.toLowerCase())
    );
}