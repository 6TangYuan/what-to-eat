<template>
    <button
        @click="toggleFavorite"
        :disabled="isLoading"
        class="favorite-icon p-2 rounded-full transition-all duration-200 hover:scale-110 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100"
        :title="isFavorited ? '取消收藏' : '收藏菜谱'"
    >
        <span class="text-xl transition-transform duration-200" :class="{ 'animate-pulse': isLoading }">
            {{ isFavorited ? '❤️' : '🤍' }}
        </span>
    </button>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Recipe } from '@/types'
import { FavoriteService } from '@/services/favoriteService'

interface Props {
    recipe: Recipe
}

const props = defineProps<Props>()

const emit = defineEmits<{
    favoriteChanged: [isFavorited: boolean]
}>()

const isLoading = ref(false)
const isFavorited = ref(false)

// 检查收藏状态
const checkFavoriteStatus = () => {
    isFavorited.value = FavoriteService.isFavorite(props.recipe.id)
}

// 切换收藏状态
const toggleFavorite = async () => {
    if (isLoading.value) return

    isLoading.value = true

    try {
        let success = false

        if (isFavorited.value) {
            // 取消收藏
            success = FavoriteService.removeFavorite(props.recipe.id)
            if (success) {
                isFavorited.value = false
                showToast('已取消收藏', 'info')
            }
        } else {
            // 添加收藏
            success = FavoriteService.addFavorite(props.recipe)
            if (success) {
                isFavorited.value = true
                showToast('已添加到收藏', 'success')
            } else {
                showToast('该菜谱已在收藏中', 'warning')
            }
        }

        if (success) {
            emit('favoriteChanged', isFavorited.value)
        }
    } catch (error) {
        console.error('收藏操作失败:', error)
        showToast('操作失败，请重试', 'error')
    } finally {
        isLoading.value = false
    }
}

// 简单的提示功能
const showToast = (message: string, type: 'success' | 'error' | 'warning' | 'info') => {
    // 创建临时提示元素
    const toast = document.createElement('div')
    toast.className = `fixed top-4 right-4 px-4 py-2 rounded-lg text-white text-sm font-medium z-50 transition-all duration-300 transform translate-x-full`

    // 根据类型设置样式
    const styles = {
        success: 'bg-green-500',
        error: 'bg-red-500',
        warning: 'bg-yellow-500',
        info: 'bg-blue-500'
    }

    toast.className += ` ${styles[type]}`
    toast.textContent = message

    document.body.appendChild(toast)

    // 显示动画
    setTimeout(() => {
        toast.style.transform = 'translateX(0)'
    }, 10)

    // 自动隐藏
    setTimeout(() => {
        toast.style.transform = 'translateX(full)'
        setTimeout(() => {
            document.body.removeChild(toast)
        }, 300)
    }, 2000)
}

onMounted(() => {
    checkFavoriteStatus()
})
</script>

<style scoped>
.favorite-icon {
    background: transparent;
    border: none;
}

.favorite-icon:hover:not(:disabled) {
    background: rgba(0, 0, 0, 0.05);
}

.favorite-icon:active:not(:disabled) {
    transform: scale(0.95);
}

@keyframes pulse {
    0%,
    100% {
        opacity: 1;
    }
    50% {
        opacity: 0.5;
    }
}

.animate-pulse {
    animation: pulse 1s ease-in-out infinite;
}
</style>
