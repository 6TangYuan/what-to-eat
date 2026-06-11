<template>
    <div class="fixed inset-0 bg-black/80 flex items-center justify-center z-50 p-2 md:p-4" @click="$emit('close')">
        <div class="bg-white rounded-lg border-2 border-[#0A0910] w-full max-w-4xl max-h-[95vh] md:max-h-[90vh] overflow-hidden" @click.stop>
            <!-- 头部 -->
            <div class="bg-blue-500 text-white p-4 flex items-center justify-between">
                <div>
                    <h3 class="font-bold text-lg">{{ image.recipeName }}</h3>
                    <p class="text-blue-100 text-sm">{{ image.cuisine }} • {{ formatDate(image.generatedAt) }}</p>
                </div>
                <button @click="$emit('close')" class="text-white hover:text-gray-200 text-2xl">×</button>
            </div>

            <!-- 图片 -->
            <div class="relative bg-black flex items-center justify-center min-h-[50vh] max-h-[80vh]">
                <img :src="image.url" :alt="image.recipeName" class="max-w-full max-h-full object-contain" />
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import type { GalleryImage } from '@/services/galleryService'

interface Props {
    image: GalleryImage
}

defineProps<Props>()

defineEmits<{
    close: []
}>()

// 格式化日期
const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    const now = new Date()
    const diffTime = now.getTime() - date.getTime()
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))

    if (diffDays === 0) {
        return '今天'
    } else if (diffDays === 1) {
        return '昨天'
    } else if (diffDays < 7) {
        return `${diffDays}天前`
    } else {
        return date.toLocaleDateString('zh-CN', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        })
    }
}
</script>

<style scoped>
/* 确保弹窗在最顶层 */
.z-50 {
    z-index: 50;
}
</style>
