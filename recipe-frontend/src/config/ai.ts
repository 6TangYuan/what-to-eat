// AI配置文件
export interface AIConfig {
    baseURL: string
    apiKey: string
    model: string
    timeout: number
    maxTokens: number
    temperature: number
}

// 菜系提示词模板
export const CUISINE_PROMPTS = {
    system: '你是一位专业的厨师，请根据用户提供的食材和菜系要求，生成详细的菜谱。',
    responseFormat: `请按照以下JSON格式返回菜谱：
{
  "name": "菜品名称",
  "ingredients": ["食材1", "食材2"],
  "steps": [
    {
      "step": 1,
      "description": "步骤描述",
      "time": 5,
      "temperature": "中火"
    }
  ],
  "cookingTime": 30,
  "difficulty": "medium",
  "tips": ["技巧1", "技巧2"]
}`
}
