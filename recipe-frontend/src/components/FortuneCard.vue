<template>
    <div class="bg-white rounded-lg border-2 border-[#333333] overflow-hidden">
        <!-- 占卜结果标题 -->
        <div class="bg-gradient-to-r from-purple-600 to-pink-600 text-white p-6">
            <div class="flex items-center justify-between">
                <div>
                    <h2 class="text-2xl font-bold mb-2">{{ fortune.dishName }}</h2>
                    <div class="flex items-center gap-4 text-sm">
                        <span class="flex items-center gap-1">
                            <span>🔮</span>
                            <span>{{ getFortuneTypeName(fortune.type) }}</span>
                        </span>
                        <span class="flex items-center gap-1">
                            <span>⏱️</span>
                            <span>{{ fortune.cookingTime }}分钟</span>
                        </span>
                        <span :class="['px-2 py-1 rounded-full text-xs font-medium', getDifficultyStyle(fortune.difficulty)]">
                            {{ getDifficultyName(fortune.difficulty) }}
                        </span>
                    </div>
                </div>
            </div>

            <!-- 幸运指数 -->
            <div class="mt-4 flex items-center gap-4">
                <div class="text-center">
                    <div class="text-xs opacity-80 mb-1">幸运指数</div>
                    <div class="flex items-center gap-1">
                        <span v-for="i in 10" :key="i" :class="['text-lg hidden sm:inline', i <= fortune.luckyIndex ? 'text-yellow-300' : 'text-white/30']">⭐</span>
                        <span class="text-xl font-bold text-yellow-300">{{ fortune.luckyIndex }}/10</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="p-4 md:p-6">
            <!-- 占卜理由 -->
            <div class="mb-6 p-4 bg-purple-50 rounded-lg border border-purple-200">
                <h3 class="font-bold text-gray-800 mb-2 flex items-center gap-2">
                    <span>🌟</span>
                    <span>占卜理由</span>
                </h3>
                <p class="text-gray-700">{{ fortune.reason }}</p>
            </div>

            <!-- 详细描述 -->
            <div class="mb-6 p-4 bg-pink-50 rounded-lg border border-pink-200">
                <h3 class="font-bold text-gray-800 mb-2 flex items-center gap-2">
                    <span>✨</span>
                    <span>神秘解析</span>
                </h3>
                <p class="text-gray-700">{{ fortune.description }}</p>
            </div>

            <!-- 食材清单 -->
            <div v-if="fortune.ingredients && fortune.ingredients.length > 0" class="mb-6">
                <h3 class="font-bold text-lg text-gray-800 mb-3 flex items-center gap-2">
                    <span>🛒</span>
                    <span>神秘食材</span>
                </h3>
                <div class="grid md:grid-cols-2 gap-2">
                    <div v-for="(ingredient, index) in fortune.ingredients" :key="index" class="flex items-center gap-2 p-2 bg-gray-50 rounded-lg">
                        <span class="w-2 h-2 bg-purple-400 rounded-full"></span>
                        <span class="text-gray-700">{{ ingredient }}</span>
                    </div>
                </div>
            </div>

            <!-- 制作步骤 -->
            <div v-if="fortune.steps && fortune.steps.length > 0" class="mb-6">
                <h3 class="font-bold text-lg text-gray-800 mb-3 flex items-center gap-2">
                    <span>👨‍🍳</span>
                    <span>神秘步骤</span>
                </h3>
                <div class="space-y-4">
                    <div v-for="(step, index) in fortune.steps" :key="index" class="flex gap-4 p-4 bg-gray-50 rounded-lg border-l-4 border-purple-400">
                        <div class="flex-shrink-0">
                            <div class="w-8 h-8 bg-purple-500 text-white rounded-full flex items-center justify-center font-bold text-sm">
                                {{ index + 1 }}
                            </div>
                        </div>
                        <div class="flex-1">
                            <p class="text-gray-800">{{ step }}</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 占卜建议 -->
            <div v-if="fortune.tips.length > 0" class="mb-6">
                <h3 class="font-bold text-lg text-gray-800 mb-3 flex items-center gap-2">
                    <span>💡</span>
                    <span>神秘建议</span>
                </h3>
                <div class="space-y-2">
                    <div v-for="(tip, index) in fortune.tips" :key="index" class="flex items-start gap-2 p-3 bg-yellow-50 rounded-lg border border-yellow-200">
                        <span class="text-yellow-600 mt-0.5">💫</span>
                        <span class="text-gray-700">{{ tip }}</span>
                    </div>
                </div>
            </div>

            <!-- 神秘话语 -->
            <div class="mb-6 p-4 bg-gradient-to-r from-purple-100 to-pink-100 rounded-lg border border-purple-200">
                <h3 class="font-bold text-gray-800 mb-2 flex items-center gap-2">
                    <span>🔮</span>
                    <span>占卜师的话</span>
                </h3>
                <p class="text-gray-700 italic text-center">{{ fortune.mysticalMessage }}</p>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import type { FortuneResult, FortuneType } from '@/types'

interface Props {
    fortune: FortuneResult
}

const { fortune } = defineProps<Props>()

// 获取占卜类型名称
const getFortuneTypeName = (type: FortuneType): string => {
    const typeNames = {
        daily: '今日运势',
        mood: '心情占卜',
        couple: '缘分配菜',
        number: '数字占卜'
    }
    return typeNames[type] || '神秘占卜'
}

// 获取难度样式
const getDifficultyStyle = (difficulty: 'easy' | 'medium' | 'hard'): string => {
    const styles = {
        easy: 'bg-green-100/20 text-green-300 border border-green-400/30',
        medium: 'bg-yellow-100/20 text-yellow-300 border border-yellow-400/30',
        hard: 'bg-red-100/20 text-red-300 border border-red-400/30'
    }
    return styles[difficulty]
}

// 获取难度名称
const getDifficultyName = (difficulty: 'easy' | 'medium' | 'hard'): string => {
    const names = {
        easy: '简单',
        medium: '中等',
        hard: '困难'
    }
    return names[difficulty]
}

// 分享结果 - 暂时未使用
// const shareResult = () => {
//     const shareText = `🔮 料理占卜师为我推荐了：${props.fortune.dishName}\n\n✨ ${props.fortune.reason}\n\n🌟 幸运指数：${props.fortune.luckyIndex}/10\n\n来"一饭封神"体验神秘的料理占卜吧！`

//     if (navigator.share) {
//         navigator.share({
//             title: '料理占卜结果',
//             text: shareText,
//             url: window.location.href
//         })
//     } else {
//         navigator.clipboard.writeText(shareText).then(() => {
//             // 可以添加提示
//             console.log('占卜结果已复制到剪贴板')
//         })
//     }
// }

// 保存结果 - 暂时未使用
// const saveResult = () => {
//     try {
//         const savedResults = JSON.parse(localStorage.getItem('fortune_results') || '[]')
//         savedResults.unshift(props.fortune)

//         // 只保留最近20个结果
//         if (savedResults.length > 20) {
//             savedResults.splice(20)
//         }

//         localStorage.setItem('fortune_results', JSON.stringify(savedResults))
//         console.log('占卜结果已保存')
//     } catch (error) {
//         console.error('保存占卜结果失败:', error)
//     }
// }
</script>
