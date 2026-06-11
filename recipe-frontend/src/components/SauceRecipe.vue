<template>
    <div class="bg-white rounded-lg border-2 border-[#333333] overflow-hidden">
        <!-- 酱料标题区域 -->
        <div class="bg-gradient-to-r from-orange-500 to-red-500 text-white p-4 md:p-6">
            <div class="flex items-center justify-between">
                <div>
                    <h2 class="text-2xl font-bold mb-2">{{ sauce.name }}</h2>
                    <div class="flex items-center gap-4 text-sm">
                        <span class="flex items-center gap-1">
                            <span>{{ getCategoryIcon(sauce.category) }}</span>
                            <span>{{ getCategoryName(sauce.category) }}</span>
                        </span>
                        <span class="flex items-center gap-1">
                            <span>⏱️</span>
                            <span>{{ sauce.makingTime }}分钟</span>
                        </span>
                        <span :class="['px-2 py-1 rounded-full text-xs font-medium', getDifficultyStyle(sauce.difficulty)]">
                            {{ getDifficultyName(sauce.difficulty) }}
                        </span>
                    </div>
                </div>
            </div>

            <!-- 口味特征 -->
            <div class="mt-4 grid grid-cols-4 gap-2 md:gap-4">
                <div class="text-center">
                    <div class="text-xs opacity-80 mb-1">🌶️ 辣度</div>
                    <div class="text-xl font-bold text-red-200">
                        {{ sauce.spiceLevel || 0 }}
                    </div>
                </div>
                <div class="text-center">
                    <div class="text-xs opacity-80 mb-1">🍯 甜度</div>
                    <div class="text-xl font-bold text-yellow-200">
                        {{ sauce.sweetLevel || 0 }}
                    </div>
                </div>
                <div class="text-center">
                    <div class="text-xs opacity-80 mb-1">🧂 咸度</div>
                    <div class="text-xl font-bold text-blue-200">
                        {{ sauce.saltLevel || 0 }}
                    </div>
                </div>
                <div class="text-center">
                    <div class="text-xs opacity-80 mb-1">🍋 酸度</div>
                    <div class="text-xl font-bold text-green-200">
                        {{ sauce.sourLevel || 0 }}
                    </div>
                </div>
            </div>
        </div>

        <div class="p-4 md:p-6">
            <!-- 酱料描述 -->
            <div v-if="sauce.description" class="mb-6 p-4 bg-orange-50 rounded-lg border border-orange-200">
                <h3 class="font-bold text-gray-800 mb-2 flex items-center gap-2">
                    <span>📝</span>
                    <span>酱料特色</span>
                </h3>
                <p class="text-gray-700">{{ sauce.description }}</p>
            </div>

            <!-- 食材清单 -->
            <div class="mb-6">
                <h3 class="font-bold text-lg text-gray-800 mb-3 flex items-center gap-2">
                    <span>🛒</span>
                    <span>食材清单</span>
                </h3>
                <div class="grid md:grid-cols-2 gap-2">
                    <div v-for="(ingredient, index) in sauce.ingredients" :key="index" class="flex items-center gap-2 p-2 bg-gray-50 rounded-lg">
                        <span class="w-2 h-2 bg-orange-400 rounded-full"></span>
                        <span class="text-gray-700">{{ ingredient }}</span>
                    </div>
                </div>
            </div>

            <!-- 制作步骤 -->
            <div class="mb-6">
                <h3 class="font-bold text-lg text-gray-800 mb-3 flex items-center gap-2">
                    <span>👨‍🍳</span>
                    <span>制作步骤</span>
                </h3>
                <div class="space-y-4">
                    <div v-for="step in sauce.steps" :key="step.step" class="flex gap-4 p-4 bg-gray-50 rounded-lg border-l-4 border-orange-400">
                        <div class="flex-shrink-0">
                            <div class="w-8 h-8 bg-orange-500 text-white rounded-full flex items-center justify-center font-bold text-sm">
                                {{ step.step }}
                            </div>
                        </div>
                        <div class="flex-1">
                            <p class="text-gray-800 mb-2">{{ step.description }}</p>
                            <div class="flex gap-4 text-sm text-gray-600">
                                <span v-if="step.time" class="flex items-center gap-1">
                                    <span>⏱️</span>
                                    <span>{{ step.time }}分钟</span>
                                </span>
                                <span v-if="step.temperature" class="flex items-center gap-1">
                                    <span>🔥</span>
                                    <span>{{ step.temperature }}</span>
                                </span>
                                <span v-if="step.technique" class="flex items-center gap-1">
                                    <span>🥄</span>
                                    <span>{{ step.technique }}</span>
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 制作技巧 -->
            <div v-if="sauce.tips.length > 0" class="mb-6">
                <h3 class="font-bold text-lg text-gray-800 mb-3 flex items-center gap-2">
                    <span>💡</span>
                    <span>制作技巧</span>
                </h3>
                <div class="space-y-2">
                    <div v-for="(tip, index) in sauce.tips" :key="index" class="flex items-start gap-2 p-3 bg-yellow-50 rounded-lg border border-yellow-200">
                        <span class="text-yellow-600 mt-0.5">💡</span>
                        <span class="text-gray-700">{{ tip }}</span>
                    </div>
                </div>
            </div>

            <!-- 保存方法 -->
            <div class="mb-6">
                <h3 class="font-bold text-lg text-gray-800 mb-3 flex items-center gap-2">
                    <span>📦</span>
                    <span>保存方法</span>
                </h3>
                <div class="p-4 bg-blue-50 rounded-lg border border-blue-200">
                    <div class="grid md:grid-cols-3 gap-4 text-sm">
                        <div class="flex items-center gap-2">
                            <span class="text-blue-600">📦</span>
                            <span class="text-gray-700">{{ sauce.storage.method }}</span>
                        </div>
                        <div class="flex items-center gap-2">
                            <span class="text-blue-600">⏰</span>
                            <span class="text-gray-700">{{ sauce.storage.duration }}</span>
                        </div>
                        <div class="flex items-center gap-2">
                            <span class="text-blue-600">🌡️</span>
                            <span class="text-gray-700">{{ sauce.storage.temperature }}</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 搭配建议 -->
            <div v-if="sauce.pairings.length > 0" class="mb-6">
                <h3 class="font-bold text-lg text-gray-800 mb-3 flex items-center gap-2">
                    <span>🍽️</span>
                    <span>搭配建议</span>
                </h3>
                <div class="flex flex-wrap gap-2">
                    <span v-for="pairing in sauce.pairings" :key="pairing" class="px-3 py-2 bg-green-100 text-green-800 rounded-full text-sm border border-green-200">
                        {{ pairing }}
                    </span>
                </div>
            </div>

            <!-- 标签 -->
            <div v-if="sauce.tags.length > 0" class="mb-4">
                <div class="flex flex-wrap gap-2">
                    <span v-for="tag in sauce.tags" :key="tag" class="px-2 py-1 bg-gray-200 text-gray-700 rounded-full text-xs"> #{{ tag }} </span>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import type { SauceRecipe } from '@/types'
import { getCategoryIcon, getCategoryName, getDifficultyStyle, getDifficultyName } from '@/utils/sauceHelpers'

interface Props {
    sauce: SauceRecipe
}

const { sauce } = defineProps<Props>()
</script>
