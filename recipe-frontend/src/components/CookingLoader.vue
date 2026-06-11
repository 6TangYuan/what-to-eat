<template>
    <div class="cooking-loader flex flex-col items-center justify-center p-8">
        <!-- 厨师动画 -->
        <div class="relative mb-6">
            <div class="cooking-animation text-6xl mb-4">👨‍🍳</div>

            <!-- 火焰效果 -->
            <div class="absolute -bottom-2 left-1/2 transform -translate-x-1/2">
                <div class="fire-animation text-2xl">🔥</div>
            </div>

            <!-- 蒸汽效果 -->
            <div class="absolute -top-4 left-1/2 transform -translate-x-1/2">
                <div class="steam-animation text-lg opacity-60">💨</div>
            </div>
        </div>

        <!-- 加载文字 -->
        <div class="text-center">
            <h3 class="text-xl font-bold text-neutral-800 mb-2">{{ currentText }}</h3>
            <div class="flex items-center justify-center space-x-1">
                <div v-for="i in 3" :key="i" class="w-2 h-2 bg-accent-500 rounded-full animate-bounce" :style="{ animationDelay: `${i * 0.2}s` }"></div>
            </div>
        </div>

        <!-- 进度条 -->
        <div class="w-64 bg-neutral-200 rounded-full h-2 mt-4 overflow-hidden">
            <div class="bg-gradient-to-r from-accent-400 to-accent-600 h-2 rounded-full transition-all duration-500 ease-out" :style="{ width: `${progress}%` }"></div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const cookingTexts = [
    '大师正在挑选食材...',
    '大师正在起火热锅...',
    '大师正在爆香配料...',
    '大师正在调制秘制酱料...',
    '大师正在掌控火候...',
    '大师正在精心摆盘...',
    '大师正在最后调味...',
    '美味佳肴即将出炉...'
]

const currentText = ref(cookingTexts[0])
const progress = ref(0)
let textInterval: NodeJS.Timeout | null = null
let progressInterval: NodeJS.Timeout | null = null

onMounted(() => {
    let textIndex = 0
    let currentProgress = 0

    // 文字轮播
    textInterval = setInterval(() => {
        textIndex = (textIndex + 1) % cookingTexts.length
        currentText.value = cookingTexts[textIndex]
    }, 2000)

    // 进度条动画
    progressInterval = setInterval(() => {
        if (currentProgress < 95) {
            currentProgress += Math.random() * 15 + 5
            progress.value = Math.min(currentProgress, 95)
        }
    }, 800)
})

onUnmounted(() => {
    if (textInterval) clearInterval(textInterval)
    if (progressInterval) clearInterval(progressInterval)
})
</script>

<style scoped>
.cooking-loader {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    border: 2px solid rgba(249, 115, 22, 0.2);
}
</style>
