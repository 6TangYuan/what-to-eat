<template>
    <div>
        <!-- 悬浮按钮 -->
        <button
            class="fixed bottom-6 right-6 z-40 w-14 h-14 rounded-full bg-gradient-to-br from-yellow-400 to-orange-500 border-2 border-[#0A0910] shadow-xl flex items-center justify-center hover:scale-105 transition-transform md:w-16 md:h-16"
            @click="toggleOpen"
            :aria-label="isOpen ? '关闭厨神助理' : '打开厨神助理'"
        >
            <div class="text-2xl md:text-3xl relative">
                <span :class="{ 'opacity-0': isLoading }">👨‍🍳</span>
                <span v-if="isLoading" class="absolute inset-0 flex items-center justify-center">
                    <span class="animate-pulse">🤔</span>
                </span>
            </div>
        </button>

        <!-- 遮罩层 -->
        <div v-if="isOpen" class="fixed inset-0 z-30 bg-black/40 backdrop-blur-[1px]" @click="toggleOpen" />

        <!-- 对话框 -->
        <div
            v-if="isOpen"
            class="fixed z-40 bg-white border-2 border-[#0A0910] md:rounded-xl shadow-2xl overflow-hidden flex flex-col"
            :class="{
                'bottom-24 right-6 w-[26rem] sm:w-[32rem] max-h-[80vh]': !isMobile,
                'inset-0 h-screen w-screen rounded-none': isMobile
            }"
        >
            <!-- 头部 -->
            <div class="px-4 py-3 bg-yellow-100 border-b-2 border-[#0A0910] flex items-center justify-between shrink-0">
                <div class="flex items-center gap-2">
                    <span class="text-xl">👨‍🍳</span>
                    <div>
                        <div class="text-sm font-black text-gray-800">厨神小助手</div>
                        <div class="text-xs text-gray-600">会做饭的AI，问我任何烹饪问题～</div>
                    </div>
                </div>
                <button class="p-1 text-gray-600 hover:text-gray-900" @click="toggleOpen">✕</button>
            </div>

            <!-- 消息列表 -->
            <div
                ref="scrollContainer"
                class="flex-1 overflow-y-auto p-3 space-y-3 bg-gray-50 flex flex-col"
                :class="{ 'min-h-[60vh] md:min-h-[60vh]': messages.length <= 1 && !isMobile }"
            >
                <div v-for="(m, idx) in messages" :key="idx" class="flex" :class="m.role === 'user' ? 'justify-end' : 'justify-start'">
                    <div v-if="m.role === 'assistant'" class="max-w-[80%] rounded-lg px-3 py-2 text-sm leading-6 bg-white border-2 border-[#0A0910] text-gray-800 markdown-body">
                        <div v-if="isLoading && idx === messages.length - 1">
                            <span class="animate-pulse">👨‍🍳</span>
                            <span class="text-sm font-medium">小助手正在思考中···</span>
                        </div>
                        <div v-else v-html="renderMarkdown(m.content)" />
                    </div>
                    <div v-else class="max-w-[80%] rounded-lg px-3 py-2 text-sm whitespace-pre-wrap leading-6 bg-yellow-300 border-2 border-[#0A0910] text-gray-900">
                        {{ m.content }}
                    </div>
                </div>
            </div>

            <!-- 输入区 -->
            <form class="border-t-2 border-gray-200 p-2 bg-white shrink-0" @submit.prevent="handleSend">
                <div class="flex items-end gap-2">
                    <textarea
                        v-model="input"
                        class="flex-1 resize-none h-12 max-h-36 px-3 py-2 text-sm border-2 border-[#0A0910] rounded-lg focus:outline-none focus:ring-2 focus:ring-yellow-400"
                        placeholder="想吃啥？怎么做？食材替换？来问我～"
                        :disabled="isLoading"
                        @keydown.enter.exact.prevent="handleSend"
                    />
                    <button
                        type="submit"
                        class="px-3 py-2 text-sm font-bold rounded-lg border-2 border-[#0A0910] bg-yellow-400 text-gray-900 hover:brightness-95 disabled:opacity-60"
                        :disabled="!input.trim() || isLoading"
                    >
                        发送
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { chatStream } from '@/services/aiService'
import MarkdownIt from 'markdown-it'

const isOpen = ref(false)
const isMobile = ref(false)

onMounted(() => {
    // 检测移动设备
    const checkMobile = () => {
        isMobile.value = window.innerWidth < 768
    }
    checkMobile()
    window.addEventListener('resize', checkMobile)
})
const messages = ref([{ role: 'assistant', content: '你好！告诉我你有什么食材/口味/菜名，我来帮你出招～' }])
const input = ref('')
const isLoading = ref(false)
const scrollContainer = ref(null)

const toggleOpen = () => {
    isOpen.value = !isOpen.value
    if (isOpen.value) {
        nextTick(scrollToBottom)
    }
}

const scrollToBottomImmediate = () => {
    if (!scrollContainer.value) return
    const container = scrollContainer.value
    // 即时滚动到底部，减去8px确保完全滚动
    container.scrollTop = container.scrollHeight - 8
}

const scrollToBottom = () => {
    if (!scrollContainer.value) return
    const container = scrollContainer.value
    // 使用平滑滚动并确保完全滚动到底部，减去8px确保完全滚动
    container.scrollTo({
        top: container.scrollHeight - 8,
        behavior: 'smooth'
    })
}

const handleSend = async () => {
    const text = input.value.trim()
    if (!text || isLoading.value) return

    // 先添加消息再滚动
    messages.value.push({ role: 'user', content: text })
    input.value = ''
    isLoading.value = true
    // 确保DOM更新后滚动
    await nextTick()
    scrollToBottom()

    try {
        const history = buildHistory()
        messages.value.push({ role: 'assistant', content: '' })
        let firstChunk = true
        await chatStream(history, delta => {
            if (firstChunk) {
                isLoading.value = false
                firstChunk = false
            }
            // 直接存储原始数据，在渲染时统一解码
            messages.value[messages.value.length - 1].content += delta
            // 使用requestAnimationFrame优化滚动性能
            requestAnimationFrame(scrollToBottomImmediate)
        })
    } catch (e) {
        isLoading.value = false
    }
}

const buildHistory = () => {
    const systemPrompt = {
        role: 'system',
        content: '你是一位专业而风趣的中华料理大师与厨房助理。简洁友好地回答与烹饪、食材替代、口味调配、厨具与火候、餐酒搭配相关的问题。尽量用要点列举，必要时给出分步操作。'
    }
    return [systemPrompt, ...messages.value.filter(m => m.role !== 'system')]
}

// 简易Markdown渲染（安全转义 + 常用语法）
const escapeHtml = str => {
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#39;')
}

const renderMarkdown = md => {
    if (!md) return ''
    // 先处理换行符
    let processedMd = md.replace(/<br\s*\/?>/gi, '\n')
    const lines = processedMd.split('\n')
    let html = ''
    let inList = false
    let inCode = false
    let codeBuffer = []
    let paraBuffer = []

    const flushParagraph = () => {
        if (paraBuffer.length > 0) {
            const text = paraBuffer.join('\n')
            html += `<p>${inline(text)}</p>`
            paraBuffer = []
        }
    }

    const flushList = () => {
        if (inList) {
            html += '</ul>'
            inList = false
        }
    }

    const inline = s => {
        let t = s
        // 先处理inline code避免转义
        t = t.replace(/`([^`]+)`/g, '<code>$1</code>')
        // bold **text** or __text__
        t = t.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
        t = t.replace(/__([^_]+)__/g, '<strong>$1</strong>')
        // italic *text* or _text_
        t = t.replace(/(^|\W)\*([^*]+)\*(?=\W|$)/g, '$1<em>$2</em>')
        t = t.replace(/(^|\W)_([^_]+)_(?=\W|$)/g, '$1<em>$2</em>')
        // 转义除markdown生成标签外的HTML
        return t.replace(/<(?!\/?(strong|em|code|ul|li|h[1-3]|p)\b)[^>]+>/g, escapeHtml)
    }

    for (const raw of lines) {
        const line = raw.replace(/\r$/, '')

        // fenced code
        if (line.trim().startsWith('```')) {
            if (!inCode) {
                flushParagraph()
                flushList()
                inCode = true
                codeBuffer = []
            } else {
                const codeHtml = escapeHtml(codeBuffer.join('\n'))
                html += `<pre class="md-pre"><code>${codeHtml}</code></pre>`
                inCode = false
                codeBuffer = []
            }
            continue
        }
        if (inCode) {
            codeBuffer.push(line)
            continue
        }

        // empty line => flush blocks
        if (line.trim() === '') {
            flushParagraph()
            flushList()
            continue
        }

        // headings
        if (/^###\s+/.test(line)) {
            flushParagraph()
            flushList()
            html += `<h3>${inline(line.replace(/^###\s+/, ''))}</h3>`
            continue
        }
        if (/^##\s+/.test(line)) {
            flushParagraph()
            flushList()
            html += `<h2>${inline(line.replace(/^##\s+/, ''))}</h2>`
            continue
        }
        if (/^#\s+/.test(line)) {
            flushParagraph()
            flushList()
            html += `<h1>${inline(line.replace(/^#\s+/, ''))}</h1>`
            continue
        }

        // list item
        if (/^\s*[-*]\s+/.test(line)) {
            flushParagraph()
            if (!inList) {
                inList = true
                html += '<ul>'
            }
            html += `<li>${inline(line.replace(/^\s*[-*]\s+/, ''))}</li>`
            continue
        }

        // normal paragraph content
        paraBuffer.push(line)
    }

    // flush tail
    if (inCode) {
        const codeHtml = escapeHtml(codeBuffer.join('\n'))
        html += `<pre class="md-pre"><code>${codeHtml}</code></pre>`
    }
    flushParagraph()
    flushList()

    return html
}
</script>

<style scoped>
/* 额外小样式：优化滚动条 */
::-webkit-scrollbar {
    width: 8px;
}
::-webkit-scrollbar-thumb {
    background: #e5e7eb;
    border-radius: 6px;
}

/* markdown基础样式 */
.markdown-body h1,
.markdown-body h2,
.markdown-body h3 {
    font-weight: 800;
    margin: 0.25rem 0 0.25rem;
}
.markdown-body h1 {
    font-size: 1.1rem;
}
.markdown-body h2 {
    font-size: 1.05rem;
}
.markdown-body h3 {
    font-size: 1rem;
}
.markdown-body p {
    margin: 0.25rem 0;
}
.markdown-body ul {
    margin: 0.25rem 0 0.25rem 1rem;
    list-style: disc;
}
.markdown-body li {
    margin: 0.125rem 0;
}
.markdown-body code {
    background: #f3f4f6;
    padding: 0.1rem 0.25rem;
    border-radius: 4px;
    font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
}
.md-pre {
    background: #111827;
    color: #e5e7eb;
    padding: 0.5rem 0.75rem;
    border-radius: 0.5rem;
    overflow: auto;
    border: 1px solid #0a0910;
}
</style>
