import type { Recipe } from '@/types'

export interface GalleryImage {
    id: string
    url: string
    recipeName: string
    recipeId: string
    cuisine: string
    ingredients: string[]
    generatedAt: string
    prompt?: string
}

export interface GalleryStats {
    total: number
    cuisineStats: Record<string, number>
    latestGenerated?: string
}

class GalleryServiceClass {
    private readonly STORAGE_KEY = 'recipe-gallery-images'

    // 获取所有图库图片
    getGalleryImages(): GalleryImage[] {
        try {
            const stored = localStorage.getItem(this.STORAGE_KEY)
            return stored ? JSON.parse(stored) : []
        } catch (error) {
            console.error('获取图库数据失败:', error)
            return []
        }
    }

    // 添加图片到图库
    addToGallery(recipe: Recipe, imageUrl: string, imageId: string, prompt?: string): boolean {
        try {
            const images = this.getGalleryImages()

            // 检查是否已存在相同的图片
            const existingIndex = images.findIndex(img => img.id === imageId)

            const galleryImage: GalleryImage = {
                id: imageId,
                url: imageUrl,
                recipeName: recipe.name,
                recipeId: recipe.id,
                cuisine: recipe.cuisine,
                ingredients: recipe.ingredients,
                generatedAt: new Date().toISOString(),
                prompt
            }

            if (existingIndex >= 0) {
                // 更新现有图片
                images[existingIndex] = galleryImage
            } else {
                // 添加新图片到开头
                images.unshift(galleryImage)
            }

            localStorage.setItem(this.STORAGE_KEY, JSON.stringify(images))
            return true
        } catch (error) {
            console.error('添加图片到图库失败:', error)
            return false
        }
    }

    // 从图库删除图片
    removeFromGallery(imageId: string): boolean {
        try {
            const images = this.getGalleryImages()
            const filteredImages = images.filter(img => img.id !== imageId)
            localStorage.setItem(this.STORAGE_KEY, JSON.stringify(filteredImages))
            return true
        } catch (error) {
            console.error('从图库删除图片失败:', error)
            return false
        }
    }

    // 清空图库
    clearGallery(): boolean {
        try {
            localStorage.removeItem(this.STORAGE_KEY)
            return true
        } catch (error) {
            console.error('清空图库失败:', error)
            return false
        }
    }

    // 获取图库统计信息
    getGalleryStats(): GalleryStats {
        const images = this.getGalleryImages()

        const cuisineStats: Record<string, number> = {}
        images.forEach(img => {
            cuisineStats[img.cuisine] = (cuisineStats[img.cuisine] || 0) + 1
        })

        return {
            total: images.length,
            cuisineStats,
            latestGenerated: images.length > 0 ? images[0].generatedAt : undefined
        }
    }

    // 根据菜系筛选图片
    getImagesByCuisine(cuisine: string): GalleryImage[] {
        return this.getGalleryImages().filter(img => img.cuisine === cuisine)
    }

    // 搜索图片
    searchImages(query: string): GalleryImage[] {
        const images = this.getGalleryImages()
        const lowerQuery = query.toLowerCase()

        return images.filter(
            img =>
                img.recipeName.toLowerCase().includes(lowerQuery) ||
                img.cuisine.toLowerCase().includes(lowerQuery) ||
                img.ingredients.some(ingredient => ingredient.toLowerCase().includes(lowerQuery))
        )
    }

    // 获取最近生成的图片
    getRecentImages(limit: number = 10): GalleryImage[] {
        return this.getGalleryImages().slice(0, limit)
    }
}

export const GalleryService = new GalleryServiceClass()
