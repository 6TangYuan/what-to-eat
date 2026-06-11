<template>
    <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4" @click.self="$emit('close')">
        <div class="bg-white rounded-lg border-2 border-[#0A0910] max-w-md w-full">
            <div class="border-b-2 border-black p-4">
                <h3 class="text-lg font-bold">编辑备注</h3>
            </div>
            <div class="p-4">
                <div class="mb-4">
                    <label class="block text-sm font-medium text-gray-700 mb-2">菜谱：{{ favorite.recipe.name }}</label>
                    <textarea
                        v-model="notes"
                        placeholder="添加你的备注..."
                        class="w-full p-3 border-2 border-[#0A0910] rounded-lg resize-none focus:outline-none focus:ring-2 focus:ring-red-400"
                        rows="4"
                        maxlength="200"
                    ></textarea>
                    <div class="text-xs text-gray-500 mt-1">{{ notes.length }}/200</div>
                </div>
                <div class="flex gap-2">
                    <button
                        @click="$emit('save', notes)"
                        class="flex-1 px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded-lg font-medium border-2 border-[#0A0910] transition-all duration-200"
                    >
                        保存
                    </button>
                    <button
                        @click="$emit('close')"
                        class="flex-1 px-4 py-2 bg-gray-500 hover:bg-gray-600 text-white rounded-lg font-medium border-2 border-[#0A0910] transition-all duration-200"
                    >
                        取消
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import type { FavoriteRecipe } from '@/types'

interface Props {
    favorite: FavoriteRecipe
}

const props = defineProps<Props>()
defineEmits<{
    close: []
    save: [notes: string]
}>()

const notes = ref(props.favorite?.notes || '')

// 监听favorite变化，更新notes
watch(
    () => props.favorite,
    newFavorite => {
        notes.value = newFavorite?.notes || ''
    },
    { immediate: true }
)
</script>
