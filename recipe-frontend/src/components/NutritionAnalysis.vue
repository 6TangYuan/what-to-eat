<template>
    <div v-if="nutritionAnalysis" class="nutrition-analysis">
        <div class="bg-green-50 border border-green-200 rounded-lg p-4 space-y-4">
            <!-- 基础营养信息 -->
            <div class="grid grid-cols-2 gap-3">
                <div class="bg-white rounded-lg p-3 border border-green-300">
                    <div class="text-center">
                        <div class="text-2xl font-bold text-orange-600">{{ nutritionAnalysis.nutrition.calories }}</div>
                        <div class="text-xs text-gray-600">卡路里</div>
                    </div>
                </div>
                <div class="bg-white rounded-lg p-3 border border-green-300">
                    <div class="text-center">
                        <div class="text-lg font-bold text-green-600">{{ nutritionAnalysis.healthScore }}/10</div>
                        <div class="text-xs text-gray-600">健康评分</div>
                    </div>
                </div>
            </div>

            <!-- 营养成分详情 -->
            <div class="bg-white rounded-lg p-3 border border-green-300">
                <h5 class="text-xs font-bold text-gray-700 mb-2">营养成分 ({{ nutritionAnalysis.servingSize }})</h5>
                <div class="grid grid-cols-2 gap-2 text-xs">
                    <div class="flex justify-between">
                        <span class="text-gray-600">蛋白质</span>
                        <span class="font-medium">{{ nutritionAnalysis.nutrition.protein }}g</span>
                    </div>
                    <div class="flex justify-between">
                        <span class="text-gray-600">碳水化合物</span>
                        <span class="font-medium">{{ nutritionAnalysis.nutrition.carbs }}g</span>
                    </div>
                    <div class="flex justify-between">
                        <span class="text-gray-600">脂肪</span>
                        <span class="font-medium">{{ nutritionAnalysis.nutrition.fat }}g</span>
                    </div>
                    <div class="flex justify-between">
                        <span class="text-gray-600">膳食纤维</span>
                        <span class="font-medium">{{ nutritionAnalysis.nutrition.fiber }}g</span>
                    </div>
                    <div class="flex justify-between">
                        <span class="text-gray-600">钠</span>
                        <span class="font-medium">{{ nutritionAnalysis.nutrition.sodium }}mg</span>
                    </div>
                    <div class="flex justify-between">
                        <span class="text-gray-600">糖</span>
                        <span class="font-medium">{{ nutritionAnalysis.nutrition.sugar }}g</span>
                    </div>
                    <div v-if="nutritionAnalysis.nutrition.vitaminC" class="flex justify-between">
                        <span class="text-gray-600">维生素C</span>
                        <span class="font-medium">{{ nutritionAnalysis.nutrition.vitaminC }}mg</span>
                    </div>
                    <div v-if="nutritionAnalysis.nutrition.calcium" class="flex justify-between">
                        <span class="text-gray-600">钙</span>
                        <span class="font-medium">{{ nutritionAnalysis.nutrition.calcium }}mg</span>
                    </div>
                    <div v-if="nutritionAnalysis.nutrition.iron" class="flex justify-between">
                        <span class="text-gray-600">铁</span>
                        <span class="font-medium">{{ nutritionAnalysis.nutrition.iron }}mg</span>
                    </div>
                </div>
            </div>

            <!-- 饮食标签 -->
            <div v-if="nutritionAnalysis.dietaryTags.length > 0">
                <h5 class="text-xs font-bold text-gray-700 mb-2">饮食特点</h5>
                <div class="flex flex-wrap gap-1">
                    <span
                        v-for="tag in nutritionAnalysis.dietaryTags"
                        :key="tag"
                        class="bg-green-200 text-green-800 px-2 py-1 rounded-full text-xs font-medium border border-green-300"
                    >
                        {{ tag }}
                    </span>
                </div>
            </div>

            <!-- 营养均衡建议 -->
            <div v-if="nutritionAnalysis.balanceAdvice.length > 0">
                <h5 class="text-xs font-bold text-gray-700 mb-2">营养建议</h5>
                <div class="bg-blue-50 border border-blue-200 rounded p-3">
                    <ul class="space-y-1">
                        <li v-for="advice in nutritionAnalysis.balanceAdvice" :key="advice" class="flex items-start gap-2 text-blue-800">
                            <span class="text-blue-600 mt-1 text-xs">💡</span>
                            <span class="text-xs">{{ advice }}</span>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import type { NutritionAnalysis } from '@/types'

interface Props {
    nutritionAnalysis: NutritionAnalysis | undefined
}

defineProps<Props>()
</script>

<style scoped>
.nutrition-analysis {
    @apply transition-all duration-300;
}
</style>
