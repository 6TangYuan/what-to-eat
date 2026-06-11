<template>
    <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4" @click.self="$emit('cancel')">
        <div class="bg-white rounded-lg border-2 border-[#0A0910] max-w-md w-full">
            <div class="border-b-2 border-black p-4">
                <h3 class="text-lg font-bold">{{ title }}</h3>
            </div>
            <div class="p-4">
                <p class="text-gray-700 mb-6">{{ message }}</p>
                <div class="flex gap-2">
                    <button
                        @click="$emit('confirm')"
                        class="flex-1 px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded-lg font-medium border-2 border-[#0A0910] transition-all duration-200"
                    >
                        确认
                    </button>
                    <button
                        @click="$emit('cancel')"
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
interface Props {
    title: string
    message: string
}

defineProps<Props>()
defineEmits<{
    confirm: []
    cancel: []
}>()
</script>
