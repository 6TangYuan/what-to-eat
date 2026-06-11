import type { SauceCategory } from '@/types'
import { sauceCategories } from '@/config/sauces'

// 难度等级描述
const difficultyDescriptions = {
    easy: { name: '简单', color: 'text-green-600', description: '新手友好，步骤简单' },
    medium: { name: '中等', color: 'text-yellow-600', description: '需要一定经验' },
    hard: { name: '困难', color: 'text-red-600', description: '需要丰富经验和技巧' }
}

// 获取分类图标
export const getCategoryIcon = (category: SauceCategory): string => {
    const config = sauceCategories.find(c => c.id === category)
    return config?.icon || '🥄'
}

// 获取分类名称
export const getCategoryName = (category: SauceCategory): string => {
    const config = sauceCategories.find(c => c.id === category)
    return config?.name || '其他酱料'
}

// 获取分类颜色
export const getCategoryColor = (category: SauceCategory): string => {
    const config = sauceCategories.find(c => c.id === category)
    return config?.color || 'bg-gray-500'
}

// 获取难度样式
export const getDifficultyStyle = (difficulty: 'easy' | 'medium' | 'hard'): string => {
    const styles = {
        easy: 'bg-green-100 text-green-800 border-green-200',
        medium: 'bg-yellow-100 text-yellow-800 border-yellow-200',
        hard: 'bg-red-100 text-red-800 border-red-200'
    }
    return `border ${styles[difficulty]}`
}

// 获取难度名称
export const getDifficultyName = (difficulty: 'easy' | 'medium' | 'hard'): string => {
    return difficultyDescriptions[difficulty]?.name || '中等'
}

// 获取难度描述
export const getDifficultyDescription = (difficulty: 'easy' | 'medium' | 'hard'): string => {
    return difficultyDescriptions[difficulty]?.description || '需要一定经验'
}

// 格式化制作时间
export const formatMakingTime = (minutes: number): string => {
    if (minutes < 60) {
        return `${minutes}分钟`
    } else {
        const hours = Math.floor(minutes / 60)
        const remainingMinutes = minutes % 60
        return remainingMinutes > 0 ? `${hours}小时${remainingMinutes}分钟` : `${hours}小时`
    }
}

// 获取口味等级描述
export const getTasteLevelDescription = (level: number, type: 'spice' | 'sweet' | 'salt' | 'sour'): string => {
    const descriptions = {
        spice: ['不辣', '微辣', '中辣', '很辣', '超辣'],
        sweet: ['不甜', '微甜', '中甜', '很甜', '超甜'],
        salt: ['不咸', '微咸', '中咸', '很咸', '超咸'],
        sour: ['不酸', '微酸', '中酸', '很酸', '超酸']
    }
    return descriptions[type][Math.max(0, Math.min(4, level - 1))] || '适中'
}

// 生成酱料标签
export const generateSauceTags = (sauce: any): string[] => {
    const tags: string[] = []
    
    // 根据口味特征添加标签
    if (sauce.spiceLevel && sauce.spiceLevel >= 4) tags.push('超辣')
    if (sauce.spiceLevel && sauce.spiceLevel <= 1) tags.push('不辣')
    if (sauce.sweetLevel && sauce.sweetLevel >= 4) tags.push('甜口')
    if (sauce.saltLevel && sauce.saltLevel >= 4) tags.push('重口味')
    if (sauce.sourLevel && sauce.sourLevel >= 4) tags.push('酸爽')
    
    // 根据制作时间添加标签
    if (sauce.makingTime <= 15) tags.push('快手')
    if (sauce.makingTime >= 60) tags.push('慢工细活')
    
    // 根据难度添加标签
    if (sauce.difficulty === 'easy') tags.push('新手友好')
    if (sauce.difficulty === 'hard') tags.push('高难度')
    
    return tags
}

// 检查是否为素食酱料
export const isVegetarian = (ingredients: string[]): boolean => {
    const meatKeywords = ['肉', '鸡', '鱼', '虾', '蟹', '贝', '蛋', '奶', '黄油', '猪油', '牛油']
    return !ingredients.some(ingredient => 
        meatKeywords.some(keyword => ingredient.includes(keyword))
    )
}

// 估算酱料营养等级
export const estimateNutritionLevel = (ingredients: string[]): 'low' | 'medium' | 'high' => {
    const healthyKeywords = ['蔬菜', '水果', '坚果', '豆', '菌', '藻', '醋', '柠檬']
    const unhealthyKeywords = ['糖', '盐', '油', '味精', '防腐剂']
    
    const healthyCount = ingredients.filter(ingredient => 
        healthyKeywords.some(keyword => ingredient.includes(keyword))
    ).length
    
    const unhealthyCount = ingredients.filter(ingredient => 
        unhealthyKeywords.some(keyword => ingredient.includes(keyword))
    ).length
    
    if (healthyCount > unhealthyCount) return 'high'
    if (healthyCount === unhealthyCount) return 'medium'
    return 'low'
}