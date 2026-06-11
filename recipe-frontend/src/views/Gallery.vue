<template>
    <div class="min-h-screen bg-yellow-400 px-2 md:px-4 py-6">
        <!-- 全局导航 -->
        <GlobalNavigation />

        <div class="max-w-7xl mx-auto">
            <!-- 页面标题 -->
            <div class="mb-6">
                <div class="bg-blue-500 text-white px-4 py-2 rounded-t-lg border-2 border-[#0A0910] border-b-0 inline-block">
                    <span class="font-bold">封神图鉴</span>
                </div>
                <div class="bg-white border-2 border-[#0A0910] rounded-lg rounded-tl-none p-4 md:p-6">
                    <div class="flex items-center justify-between">
                        <div class="flex items-center gap-3">
                            <div class="w-12 h-12 bg-blue-500 rounded-lg flex items-center justify-center">
                                <span class="text-white text-2xl">🖼️</span>
                            </div>
                            <div>
                                <h1 class="text-md font-bold text-gray-800">AI厨艺的视觉宝典</h1>
                                <p class="text-gray-600 text-xs">共生成了 {{ images.length }} 张菜品图片</p>
                            </div>
                        </div>

                        <!-- 操作按钮 -->
                        <div class="flex items-center gap-2">
                            <button
                                v-if="images.length > 0"
                                @click="showClearConfirm = true"
                                class="px-3 py-2 bg-gray-500 hover:bg-gray-600 text-white rounded-lg text-sm font-medium border-2 border-[#0A0910] transition-all duration-200 hover:scale-105"
                            >
                                🗑️ 清空
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 搜索和筛选 -->
            <div v-if="false" class="mb-6">
                <div class="bg-white border-2 border-[#0A0910] rounded-lg p-4">
                    <div class="flex flex-col md:flex-row gap-4">
                        <!-- 搜索框 -->
                        <div class="flex-1">
                            <input
                                v-model="searchQuery"
                                placeholder="搜索菜谱名称、菜系或食材..."
                                class="w-full p-3 border-2 border-[#0A0910] rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-400"
                            />
                        </div>

                        <!-- 菜系筛选 -->
                        <div class="md:w-48">
                            <select v-model="selectedCuisine" class="w-full p-3 border-2 border-[#0A0910] rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-400">
                                <option value="">全部菜系</option>
                                <option v-for="cuisine in availableCuisines" :key="cuisine" :value="cuisine">
                                    {{ cuisine }}
                                </option>
                            </select>
                        </div>

                        <!-- 排序 -->
                        <div class="md:w-48">
                            <select v-model="sortBy" class="w-full p-3 border-2 border-[#0A0910] rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-400">
                                <option value="date-desc">最新生成</option>
                                <option value="date-asc">最早生成</option>
                                <option value="name-asc">菜名 A-Z</option>
                                <option value="name-desc">菜名 Z-A</option>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 图片网格 -->
            <div v-if="filteredImages.length > 0" class="bg-white border-2 border-[#0A0910] rounded-lg p-3 md:p-6">
                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    <div
                        v-for="image in filteredImages"
                        :key="image.id"
                        class="bg-white border-2 border-[#0A0910] rounded-lg overflow-hidden hover:shadow-lg transition-all duration-200 group"
                    >
                        <!-- 图片 -->
                        <div class="relative aspect-[4/3] overflow-hidden cursor-pointer" @click="openImageModal(image)">
                            <img
                                :src="image.url"
                                :alt="image.recipeName"
                                class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                                @error="handleImageError(image.id)"
                            />

                            <!-- 悬浮信息层 -->
                            <div
                                class="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none"
                            >
                                <!-- 顶部操作按钮 -->
                                <div class="absolute top-3 right-3 flex gap-2 pointer-events-auto">
                                    <button
                                        @click.stop="downloadImage(image)"
                                        class="p-2 bg-blue-500/80 hover:bg-blue-500 text-white rounded-full text-sm transition-colors backdrop-blur-sm"
                                        title="下载图片"
                                    >
                                        ⬇️
                                    </button>
                                    <button
                                        @click.stop="confirmDeleteImage(image.id)"
                                        class="p-2 bg-red-500/80 hover:bg-red-500 text-white rounded-full text-sm transition-colors backdrop-blur-sm"
                                        title="删除图片"
                                    >
                                        🗑️
                                    </button>
                                </div>
                            </div>

                            <!-- 底部信息 -->
                            <div class="absolute bottom-0 left-0 right-0 p-4">
                                <h3 class="font-bold text-white text-lg mb-2 line-clamp-1">{{ image.recipeName }}</h3>
                                <div class="flex items-center justify-between mb-3">
                                    <span class="text-white/90 text-sm">{{ image.cuisine }}</span>
                                    <span class="text-white/80 text-xs">{{ formatDate(image.generatedAt) }}</span>
                                </div>

                                <!-- 食材标签 -->
                                <div class="flex flex-wrap gap-1">
                                    <span
                                        v-for="ingredient in image.ingredients.slice(0, 4)"
                                        :key="ingredient"
                                        class="bg-white/20 backdrop-blur-sm text-white px-2 py-1 rounded text-xs border border-white/30"
                                    >
                                        {{ ingredient }}
                                    </span>
                                    <span v-if="image.ingredients.length > 4" class="bg-white/10 backdrop-blur-sm text-white/80 px-2 py-1 rounded text-xs border border-white/20">
                                        +{{ image.ingredients.length - 4 }}
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 空状态 -->
            <div v-else-if="images.length === 0" class="text-center py-6">
                <div class="bg-white border-2 border-[#0A0910] rounded-lg p-8">
                    <div class="text-6xl mb-4">🖼️</div>
                    <h3 class="text-xl font-bold text-gray-800 mb-2">图库还是空的</h3>
                    <p class="text-gray-600 mb-6">去生成一些菜品效果图，让图库丰富起来吧！</p>
                    <router-link
                        to="/"
                        class="inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-blue-500 to-purple-500 hover:from-blue-600 hover:to-purple-600 text-white rounded-lg font-bold border-2 border-[#0A0910] transition-all duration-200 hover:scale-105"
                    >
                        <span>✨</span>
                        <span>开始生成菜谱</span>
                    </router-link>
                </div>
            </div>

            <!-- 搜索无结果 -->
            <div v-else class="text-center py-16">
                <div class="bg-white border-2 border-[#0A0910] rounded-lg p-8">
                    <div class="text-4xl mb-4">🔍</div>
                    <h3 class="text-xl font-bold text-gray-800 mb-2">没有找到匹配的图片</h3>
                    <p class="text-gray-600 mb-4">试试调整搜索条件或筛选选项</p>
                    <button
                        @click="clearFilters"
                        class="px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg font-medium border-2 border-[#0A0910] transition-all duration-200"
                    >
                        清除筛选条件
                    </button>
                </div>
            </div>
        </div>

        <!-- 图片详情弹窗 -->
        <ImageModal v-if="selectedImage" :image="selectedImage" @close="selectedImage = null" />

        <!-- 删除确认弹窗 -->
        <ConfirmModal v-if="deletingImageId" title="确认删除图片" message="确定要删除这张图片吗？此操作不可恢复。" @confirm="deleteImage" @cancel="deletingImageId = null" />

        <!-- 清空确认弹窗 -->
        <ConfirmModal v-if="showClearConfirm" title="确认清空图库" message="确定要清空所有图片吗？此操作不可恢复。" @confirm="clearAllImages" @cancel="showClearConfirm = false" />

        <!-- 底部 -->
        <GlobalFooter />
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { GalleryService, type GalleryImage } from '@/services/galleryService'
import GlobalNavigation from '@/components/GlobalNavigation.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'
import ImageModal from '@/components/ImageModal.vue'
import ConfirmModal from '@/components/ConfirmModal.vue'

// 响应式数据
const images = ref<GalleryImage[]>([])
const searchQuery = ref('')
const selectedCuisine = ref('')
const sortBy = ref('date-desc')
const selectedImage = ref<GalleryImage | null>(null)
const deletingImageId = ref<string | null>(null)
const showClearConfirm = ref(false)

// 可用菜系列表
const availableCuisines = computed(() => {
    const cuisines = new Set(images.value.map(img => img.cuisine))
    return Array.from(cuisines).sort()
})

// 筛选后的图片列表
const filteredImages = computed(() => {
    let filtered = [...images.value]

    // 搜索筛选
    if (searchQuery.value.trim()) {
        const query = searchQuery.value.toLowerCase()
        filtered = filtered.filter(
            img =>
                img.recipeName.toLowerCase().includes(query) ||
                img.cuisine.toLowerCase().includes(query) ||
                img.ingredients.some(ingredient => ingredient.toLowerCase().includes(query))
        )
    }

    // 菜系筛选
    if (selectedCuisine.value) {
        filtered = filtered.filter(img => img.cuisine === selectedCuisine.value)
    }

    // 排序
    filtered.sort((a, b) => {
        switch (sortBy.value) {
            case 'date-desc':
                return new Date(b.generatedAt).getTime() - new Date(a.generatedAt).getTime()
            case 'date-asc':
                return new Date(a.generatedAt).getTime() - new Date(b.generatedAt).getTime()
            case 'name-asc':
                return a.recipeName.localeCompare(b.recipeName)
            case 'name-desc':
                return b.recipeName.localeCompare(a.recipeName)
            default:
                return 0
        }
    })

    return filtered
})

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
            month: 'short',
            day: 'numeric'
        })
    }
}

// 刷新图库
const refreshGallery = () => {
    images.value = GalleryService.getGalleryImages()
}

// 打开图片详情弹窗
const openImageModal = (image: GalleryImage) => {
    selectedImage.value = image
}

// 确认删除图片
const confirmDeleteImage = (imageId: string) => {
    deletingImageId.value = imageId
    selectedImage.value = null
}

// 删除图片
const deleteImage = () => {
    if (!deletingImageId.value) return

    const success = GalleryService.removeFromGallery(deletingImageId.value)
    if (success) {
        refreshGallery()
        showToast('图片已删除', 'info')
    } else {
        showToast('删除失败', 'error')
    }
    deletingImageId.value = null
}

// 清空所有图片
const clearAllImages = () => {
    const success = GalleryService.clearGallery()
    if (success) {
        refreshGallery()
        showToast('图库已清空', 'info')
    } else {
        showToast('清空失败', 'error')
    }
    showClearConfirm.value = false
}

// 下载图片
const downloadImage = (image: GalleryImage) => {
    try {
        window.open(image.url, '_blank')
        showToast('正在打开图片', 'info')
    } catch (error) {
        console.error('打开图片失败:', error)
        showToast('打开失败', 'error')
    }
}

// 处理图片加载错误
const handleImageError = (imageId: string) => {
    console.warn(`图片加载失败: ${imageId}`)
}

// 清除筛选条件
const clearFilters = () => {
    searchQuery.value = ''
    selectedCuisine.value = ''
    sortBy.value = 'date-desc'
}

// 简单的提示功能
const showToast = (message: string, type: 'success' | 'error' | 'warning' | 'info') => {
    const toast = document.createElement('div')
    toast.className = `fixed top-4 right-4 px-4 py-2 rounded-lg text-white text-sm font-medium z-50 transition-all duration-300 transform translate-x-full`

    const styles = {
        success: 'bg-green-500',
        error: 'bg-red-500',
        warning: 'bg-yellow-500',
        info: 'bg-blue-500'
    }

    toast.className += ` ${styles[type]}`
    toast.textContent = message

    document.body.appendChild(toast)

    setTimeout(() => {
        toast.style.transform = 'translateX(0)'
    }, 10)

    setTimeout(() => {
        toast.style.transform = 'translateX(full)'
        setTimeout(() => {
            document.body.removeChild(toast)
        }, 300)
    }, 2000)
}

// 初始化
onMounted(() => {
    refreshGallery()
})
</script>

<style scoped>
.line-clamp-1 {
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

/* 响应式调整 */
@media (max-width: 640px) {
    .grid-cols-1 {
        gap: 1rem;
    }
}
</style>
