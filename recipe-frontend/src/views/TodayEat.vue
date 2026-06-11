<template>
    <div class="min-h-screen bg-yellow-400 px-2 md:px-4 py-6">
        <!-- 全局导航 -->
        <GlobalNavigation />

        <div class="max-w-7xl mx-auto space-y-6 rounded-lg">
            <!-- 开始按钮 -->
            <div v-if="!isSelecting && selectedDishes.length === 0" class="text-center">
                <div class="bg-white rounded-lg shadow-lg p-8 border-2 border-[#0A0910]">
                    <div class="text-6xl mb-4">🎲</div>
                    <h2 class="text-2xl font-bold text-gray-800 mb-4">准备好了吗？</h2>

                    <button
                        @click="startRandomSelection"
                        class="px-8 py-3 bg-gradient-to-r from-orange-500 to-red-500 text-white rounded-xl font-semibold hover:from-orange-600 hover:to-red-600 transition-all transform hover:scale-105 shadow-lg mb-4"
                    >
                        {{ randomDice }} 开始随机选择
                    </button>

                    <!-- 折叠配置选项 -->
                    <div class="mt-4 mb-6">
                        <div
                            @click="showPreference = !showPreference"
                            class="text-sm text-gray-500 hover:text-gray-700 flex items-center justify-center gap-1 mx-auto cursor-pointer"
                        >
                            <span>偏好设置</span>
                            <svg
                                xmlns="http://www.w3.org/2000/svg"
                                class="h-4 w-4 transition-transform"
                                :class="{ 'transform rotate-180': showPreference }"
                                fill="none"
                                viewBox="0 0 24 24"
                                stroke="currentColor"
                            >
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                            </svg>
                        </div>

                        <div v-if="showPreference" class="bg-white rounded-xl max-w-md mx-auto p-4 mt-2 border border-gray-200">
                            <div class="grid grid-cols-2 gap-2 md:flex md:flex-row md:gap-4">
                                <button
                                    @click="preference = 'meat-heavy'"
                                    :class="preference === 'meat-heavy' ? 'bg-orange-500 text-white' : 'bg-gray-100 text-gray-800'"
                                    class="px-3 py-1.5 rounded-lg text-sm font-medium transition-colors md:flex-1"
                                >
                                    🥩 荤菜多
                                </button>
                                <button
                                    @click="preference = 'veg-heavy'"
                                    :class="preference === 'veg-heavy' ? 'bg-green-500 text-white' : 'bg-gray-100 text-gray-800'"
                                    class="px-3 py-1.5 rounded-lg text-sm font-medium transition-colors md:flex-1"
                                >
                                    🥬 素菜多
                                </button>
                                <button
                                    @click="preference = 'veg-only'"
                                    :class="preference === 'veg-only' ? 'bg-green-600 text-white' : 'bg-gray-100 text-gray-800'"
                                    class="px-3 py-1.5 rounded-lg text-sm font-medium transition-colors md:flex-1"
                                >
                                    🌱 纯素
                                </button>
                                <button
                                    @click="preference = 'meat-only'"
                                    :class="preference === 'meat-only' ? 'bg-orange-600 text-white' : 'bg-gray-100 text-gray-800'"
                                    class="px-3 py-1.5 rounded-lg text-sm font-medium transition-colors md:flex-1"
                                >
                                    🍖 纯荤
                                </button>
                            </div>
                        </div>
                    </div>
                    <hr class="mb-4" />
                    <p class="text-gray-500 text-sm italic transition">{{ randomFoodComment }}</p>
                </div>
            </div>

            <!-- 选择过程 -->
            <div v-if="isSelecting" class="bg-white rounded-2xl shadow-lg p-6">
                <div class="text-center mb-6 max-w-2xl mx-auto">
                    <h3 class="text-xl font-bold text-gray-800 mb-2">{{ selectionStatus }}</h3>
                    <div class="w-full bg-gray-200 rounded-full h-2">
                        <div class="bg-gradient-to-r from-orange-500 to-red-500 h-2 rounded-full transition-all duration-500" :style="{ width: `${selectionProgress}%` }"></div>
                    </div>
                </div>

                <!-- 当前选择显示 -->
                <div v-if="currentSelection" class="text-center p-6 bg-gray-50 rounded-xl">
                    <div class="text-4xl mb-2">{{ currentSelection.type === 'dish' ? '🍽️' : currentSelection.avatar }}</div>
                    <div class="text-lg font-semibold text-gray-800">{{ currentSelection.name }}</div>
                    <div v-if="currentSelection.specialty" class="text-sm text-gray-600">{{ currentSelection.specialty }}</div>
                </div>
            </div>

            <!-- 选择结果 -->
            <div v-if="!isSelecting && selectedDishes.length > 0" class="bg-white rounded-2xl shadow-lg p-6 border-2 border-[#0A0910]">
                <h3 class="text-xl font-bold text-gray-800 mb-6 text-center">🎉 今日推荐</h3>

                <div class="grid md:grid-cols-2 gap-6 mb-6">
                    <!-- 菜品 -->
                    <div class="bg-green-50 rounded-xl p-4">
                        <h4 class="font-semibold text-green-800 mb-3 flex items-center gap-2">
                            <span>🥗</span>
                            <span>推荐菜品 ({{ selectedDishes.length }}道)</span>
                        </h4>
                        <div class="grid grid-cols-2 gap-2">
                            <div v-for="dish in selectedDishes" :key="dish" class="bg-white border-2 border-green-200 rounded-lg p-2 hover:bg-green-100 transition-colors">
                                <div class="text-sm font-medium text-green-800 text-center">{{ dish }}</div>
                            </div>
                        </div>
                    </div>

                    <!-- 大师 -->
                    <div class="bg-purple-50 rounded-xl p-4">
                        <h4 class="font-semibold text-purple-800 mb-3 flex items-center gap-2">
                            <span>👨‍🍳</span>
                            <span>推荐主厨</span>
                        </h4>
                        <div class="flex items-center gap-3">
                            <div class="text-3xl">{{ selectedMaster?.avatar }}</div>
                            <div>
                                <div class="font-semibold text-purple-800">{{ selectedMaster?.name }}</div>
                                <div class="text-sm text-purple-600">{{ selectedMaster?.specialty }}</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 操作按钮 -->
                <div class="flex flex-col sm:flex-row gap-4 justify-center">
                    <button
                        @click="generateRecipeFromSelection"
                        :disabled="isGenerating"
                        class="px-6 py-3 bg-gradient-to-r from-blue-500 to-purple-500 text-white rounded-xl font-semibold hover:from-blue-600 hover:to-purple-600 transition-all transform hover:scale-105 shadow-lg disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
                    >
                        <span v-if="isGenerating" class="flex items-center gap-2">
                            <div class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                            <span>{{ generatingText }}</span>
                        </span>
                        <span v-else class="flex items-center gap-2">
                            <span>✨</span>
                            <span>生成菜谱</span>
                        </span>
                    </button>

                    <button
                        @click="resetSelection"
                        :disabled="isGenerating"
                        class="px-6 py-3 bg-gray-500 text-white rounded-xl font-semibold hover:bg-gray-600 transition-all transform hover:scale-105 shadow-lg disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
                    >
                        🎲 重新选择
                    </button>
                </div>
            </div>

            <!-- 菜谱结果 -->
            <div v-if="recipe" class="bg-white rounded-2xl shadow-lg p-4 md:p-6 border-2 border-[#0A0910]">
                <h3 class="text-xl font-bold text-gray-800 mb-6 text-center flex items-center justify-center gap-2">
                    <span>📖</span>
                    <span>专属菜谱</span>
                </h3>
                <div class="max-w-2xl mx-auto border-2 border-[#333333] rounded-lg overflow-hidden">
                    <RecipeCard :recipe="recipe" />
                </div>
            </div>
        </div>

        <!-- 底部 -->
        <GlobalFooter />
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { cuisines } from '@/config/cuisines'
import { ingredientCategories } from '@/config/ingredients'
import type { Recipe, CuisineType } from '@/types'
import { generateRecipe } from '@/services/aiService'
import RecipeCard from '@/components/RecipeCard.vue'
import GlobalNavigation from '@/components/GlobalNavigation.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'

// 状态管理
const isSelecting = ref(false)
const isGenerating = ref(false)
const selectedDishes = ref<string[]>([])
const selectedMaster = ref<CuisineType | null>(null)
const recipe = ref<Recipe | null>(null)
const preference = ref<string | null>(null)
const showPreference = ref(false)

// 选择过程状态
const selectionStatus = ref('')
const selectionProgress = ref(0)
const currentSelection = ref<any>(null)

// 文字轮播
const generatingText = ref('正在生成菜谱...')
const generatingTexts = ['正在生成菜谱...', '大师正在创作...', '调配独特配方...', '完善制作步骤...']

// 随机筛子表情
const diceEmojis = ['🎯']
const randomDice = ref('🎯')

// 美食评论
const foodComments = [
    "💬 鲁菜大师看到我的五花肉，直接拍案而起：'今天必须教你什么叫真正的把子肉！' 🐷🔥",
    "💬 川菜大师盯着我的鸡胸肉冷笑：'莫得问题，马上让你体验什么叫麻辣鸡丝怀疑人生' 🌶️😭",
    '💬 给粤菜大师一根白萝卜，他能还你一桌国宴级开水白菜...而我只会凉拌 🦢🤷',
    '💬 日料大师处理我的三文鱼时，刀光闪过，鱼生薄得能当手机贴膜 🍣📱',
    "💬 湘菜大师的炒锅起火三米高：'辣椒放少了！你这是对湖南人的侮辱！' 🔥🌶️",
    "💬 当法餐大师看到我用速冻牛排：'亲爱的，这需要先做个SPA再按摩48小时' 🥩💆",
    '💬 闽菜大师的海鲜汤里，虾兵蟹将都在跳佛跳墙 🦐🙏',
    '💬 意大利面在真正意厨手里旋转的样子，比我前任还会绕 🍝💔',
    '💬 徽菜大师的火腿吊汤术，香得邻居以为我家在炼丹 🐷☁️',
    '💬 泰式大师的冬阴功里，柠檬草、香茅、南姜正在开演唱会 🎤🌿'
]

const currentFoodComment = ref(foodComments[0])
const randomFoodComment = computed(() => currentFoodComment.value)

onMounted(() => {
    const commentInterval = setInterval(() => {
        currentFoodComment.value = foodComments[Math.floor(Math.random() * foodComments.length)]
    }, 3000)

    onUnmounted(() => clearInterval(commentInterval))
})

// 所有菜品数据
const allDishes = ref<string[]>([])

// 初始化
onMounted(() => {
    allDishes.value = ingredientCategories.flatMap(category => category.items)
    randomDice.value = diceEmojis[Math.floor(Math.random() * diceEmojis.length)]
})

// 开始随机选择
const startRandomSelection = async () => {
    isSelecting.value = true
    selectedDishes.value = []
    selectedMaster.value = null
    recipe.value = null
    selectionProgress.value = 0

    // 第一阶段：选择菜品
    selectionStatus.value = '正在随机选择菜品...'
    await selectRandomDishes()

    // 第二阶段：选择大师
    selectionStatus.value = '正在匹配主厨大师...'
    await selectRandomMaster()

    // 完成
    selectionStatus.value = '选择完成！'
    selectionProgress.value = 100

    setTimeout(() => {
        isSelecting.value = false
    }, 1000)
}

// 随机选择菜品
const selectRandomDishes = async () => {
    const dishCount = 6 // 固定生成6个菜品
    let filteredDishes = [...allDishes.value]

    // 根据偏好过滤菜品
    if (preference.value) {
        const meatCategories = ['meat', 'seafood']
        const vegCategories = ['vegetables', 'mushrooms', 'beans']

        if (preference.value === 'meat-heavy') {
            filteredDishes = filteredDishes.filter(dish => ingredientCategories.some(cat => meatCategories.includes(cat.id) && cat.items.includes(dish)))
        } else if (preference.value === 'veg-heavy') {
            filteredDishes = filteredDishes.filter(dish => ingredientCategories.some(cat => vegCategories.includes(cat.id) && cat.items.includes(dish)))
        } else if (preference.value === 'meat-only') {
            filteredDishes = filteredDishes.filter(dish => ingredientCategories.some(cat => meatCategories.includes(cat.id) && cat.items.includes(dish)))
        } else if (preference.value === 'veg-only') {
            filteredDishes = filteredDishes.filter(dish => ingredientCategories.some(cat => vegCategories.includes(cat.id) && cat.items.includes(dish)))
        }
    }

    const shuffledDishes = [...filteredDishes].sort(() => 0.5 - Math.random())
    const uniqueDishes = new Set<string>()

    // 确保获取4个不同的菜品
    while (uniqueDishes.size < dishCount && shuffledDishes.length > 0) {
        const dish = shuffledDishes.pop()
        if (dish) uniqueDishes.add(dish)
    }

    // 模拟选择过程
    for (let i = 0; i < 5; i++) {
        const randomDish = [...uniqueDishes][Math.floor(Math.random() * uniqueDishes.size)]
        currentSelection.value = {
            type: 'dish',
            name: randomDish
        }
        selectionProgress.value = (i / 5) * 50
        await new Promise(resolve => setTimeout(resolve, 100))
    }

    // 确定选择
    selectedDishes.value = [...uniqueDishes]
    currentSelection.value = {
        type: 'dish',
        name: selectedDishes.value[0]
    }

    await new Promise(resolve => setTimeout(resolve, 300))
}

// 随机选择大师
const selectRandomMaster = async () => {
    // 模拟选择过程
    for (let i = 0; i < 10; i++) {
        const randomMaster = cuisines[Math.floor(Math.random() * cuisines.length)]
        currentSelection.value = {
            type: 'master',
            name: randomMaster.name,
            avatar: randomMaster.avatar,
            specialty: randomMaster.specialty
        }
        selectionProgress.value = 50 + (i / 10) * 50
        await new Promise(resolve => setTimeout(resolve, 80))
    }

    // 确定选择
    const finalMaster = cuisines[Math.floor(Math.random() * cuisines.length)]
    selectedMaster.value = finalMaster
    currentSelection.value = {
        type: 'master',
        name: finalMaster.name,
        avatar: finalMaster.avatar,
        specialty: finalMaster.specialty
    }

    await new Promise(resolve => setTimeout(resolve, 500))
}

// 生成菜谱
const generateRecipeFromSelection = async () => {
    if (!selectedMaster.value || selectedDishes.value.length === 0 || isGenerating.value) return

    isGenerating.value = true

    // 文字轮播
    let textIndex = 0
    const textInterval = setInterval(() => {
        generatingText.value = generatingTexts[textIndex]
        textIndex = (textIndex + 1) % generatingTexts.length
    }, 1000)

    try {
        // 调用AI服务生成真实菜谱
        const cuisineInfo = {
            id: selectedMaster.value.id,
            name: selectedMaster.value.name,
            description: selectedMaster.value.description || '',
            avatar: selectedMaster.value.avatar,
            specialty: selectedMaster.value.specialty,
            prompt: selectedMaster.value.specialty
        }

        recipe.value = await generateRecipe(selectedDishes.value, cuisineInfo)

        // 显示成功提示
        showToast('菜谱生成成功', 'success')
    } catch (error) {
        console.error('生成菜谱失败:', error)
        
        // 显示友好的错误信息
        const masterName = selectedMaster.value?.name || '大师'
        const friendlyMessage = `${masterName}不会这道菜，哈哈！换个搭配试试吧~`
        showToast(friendlyMessage, 'error')
    } finally {
        clearInterval(textInterval)
        isGenerating.value = false
    }
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

// 重置选择
const resetSelection = () => {
    selectedDishes.value = []
    selectedMaster.value = null
    recipe.value = null
    currentSelection.value = null
    selectionProgress.value = 0
}
</script>

<style scoped>
/* 基础动画 */
@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(20px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

@keyframes pulse {
    0%,
    100% {
        transform: scale(1);
    }
    50% {
        transform: scale(1.05);
    }
}

@keyframes spin {
    from {
        transform: rotate(0deg);
    }
    to {
        transform: rotate(360deg);
    }
}

/* 应用动画 */
.animate-spin {
    animation: spin 1s linear infinite;
}

/* 按钮悬停效果 */
button {
    transition: all 0.3s ease;
}

button:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

button:active:not(:disabled) {
    transform: translateY(0);
    background-color: transparent;
}

/* 偏好设置特殊样式 */
div.text-sm.text-gray-500:hover {
    background-color: transparent;
}

/* 标签悬停效果 */
.bg-green-200,
.bg-purple-50 {
    transition: all 0.3s ease;
}

.bg-green-200:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(34, 197, 94, 0.2);
}

/* 进度条动画 */
.bg-gradient-to-r {
    transition: width 0.5s ease-out;
}

/* 当前选择项的脉冲效果 */
.text-4xl {
    animation: pulse 2s ease-in-out infinite;
}

/* 响应式调整 */
@media (max-width: 640px) {
    .text-4xl {
        font-size: 2rem;
    }

    .text-6xl {
        font-size: 3rem;
    }

    .px-8 {
        padding-left: 1.5rem;
        padding-right: 1.5rem;
    }
}
</style>
