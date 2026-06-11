import type { Recipe, FavoriteRecipe } from '@/types'

const FAVORITES_KEY = 'yifan-fengshen-favorites'

/**
 * 收藏服务 - 管理本地收藏的菜谱
 */
export class FavoriteService {
    /**
     * 获取所有收藏的菜谱
     */
    static getFavorites(): FavoriteRecipe[] {
        try {
            const stored = localStorage.getItem(FAVORITES_KEY)
            return stored ? JSON.parse(stored) : []
        } catch (error) {
            console.error('获取收藏列表失败:', error)
            return []
        }
    }

    /**
     * 添加菜谱到收藏
     */
    static addFavorite(recipe: Recipe, notes?: string): boolean {
        try {
            const favorites = this.getFavorites()

            // 检查是否已收藏
            if (favorites.some(fav => fav.recipe.id === recipe.id)) {
                return false // 已收藏
            }

            const favoriteRecipe: FavoriteRecipe = {
                id: `fav-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
                recipe,
                favoriteDate: new Date().toISOString(),
                notes
            }

            favorites.unshift(favoriteRecipe) // 添加到开头
            localStorage.setItem(FAVORITES_KEY, JSON.stringify(favorites))
            return true
        } catch (error) {
            console.error('添加收藏失败:', error)
            return false
        }
    }

    /**
     * 从收藏中移除菜谱
     */
    static removeFavorite(recipeId: string): boolean {
        try {
            const favorites = this.getFavorites()
            const filteredFavorites = favorites.filter(fav => fav.recipe.id !== recipeId)

            if (filteredFavorites.length === favorites.length) {
                return false // 未找到要删除的项
            }

            localStorage.setItem(FAVORITES_KEY, JSON.stringify(filteredFavorites))
            return true
        } catch (error) {
            console.error('移除收藏失败:', error)
            return false
        }
    }

    /**
     * 检查菜谱是否已收藏
     */
    static isFavorite(recipeId: string): boolean {
        try {
            const favorites = this.getFavorites()
            return favorites.some(fav => fav.recipe.id === recipeId)
        } catch (error) {
            console.error('检查收藏状态失败:', error)
            return false
        }
    }

    /**
     * 更新收藏备注
     */
    static updateFavoriteNotes(recipeId: string, notes: string): boolean {
        try {
            const favorites = this.getFavorites()
            const favoriteIndex = favorites.findIndex(fav => fav.recipe.id === recipeId)

            if (favoriteIndex === -1) {
                return false
            }

            favorites[favoriteIndex].notes = notes
            localStorage.setItem(FAVORITES_KEY, JSON.stringify(favorites))
            return true
        } catch (error) {
            console.error('更新收藏备注失败:', error)
            return false
        }
    }

    /**
     * 清空所有收藏
     */
    static clearAllFavorites(): boolean {
        try {
            localStorage.removeItem(FAVORITES_KEY)
            return true
        } catch (error) {
            console.error('清空收藏失败:', error)
            return false
        }
    }

    /**
     * 获取收藏统计信息
     */
    static getFavoriteStats() {
        const favorites = this.getFavorites()
        const cuisineStats: Record<string, number> = {}

        favorites.forEach(fav => {
            const cuisine = fav.recipe.cuisine
            cuisineStats[cuisine] = (cuisineStats[cuisine] || 0) + 1
        })

        return {
            total: favorites.length,
            cuisineStats,
            latestFavorite: favorites[0]?.favoriteDate
        }
    }
}
