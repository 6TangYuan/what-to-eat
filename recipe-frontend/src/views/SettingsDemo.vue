<template>
    <div class="min-h-screen bg-gray-50 px-4 py-6">
        <!-- 全局导航 -->
        <GlobalNavigation />

        <div class="max-w-4xl mx-auto space-y-6">
            <!-- 页面标题 -->
            <div class="bg-white rounded-lg shadow-lg p-6 border-2 border-[#0A0910]">
                <h1 class="text-3xl font-bold text-gray-800 mb-2">⚙️ 配置系统演示</h1>
                <p class="text-gray-600">这里展示了新的配置系统功能，包括设置管理、配置测试等。</p>
            </div>

            <!-- 配置测试组件 -->
            <ConfigTest />

            <!-- 使用说明 -->
            <div class="bg-white rounded-lg shadow-lg p-6 border-2 border-[#0A0910]">
                <h2 class="text-2xl font-bold text-gray-800 mb-4">📖 使用说明</h2>

                <div class="space-y-4">
                    <div class="bg-blue-50 rounded-lg p-4">
                        <h3 class="font-semibold text-blue-800 mb-2">1. 打开设置</h3>
                        <p class="text-blue-700">点击导航栏右侧的齿轮图标打开设置弹窗</p>
                    </div>

                    <div class="bg-green-50 rounded-lg p-4">
                        <h3 class="font-semibold text-green-800 mb-2">2. 修改配置</h3>
                        <p class="text-green-700">在弹窗中修改API地址、密钥、模型等配置项</p>
                    </div>

                    <div class="bg-purple-50 rounded-lg p-4">
                        <h3 class="font-semibold text-purple-800 mb-2">3. 保存设置</h3>
                        <p class="text-purple-700">点击"保存设置"按钮，配置会立即生效并保存到本地</p>
                    </div>

                    <div class="bg-orange-50 rounded-lg p-4">
                        <h3 class="font-semibold text-orange-800 mb-2">4. 测试配置</h3>
                        <p class="text-orange-700">使用上方的"测试文本生成"按钮验证配置是否正确</p>
                    </div>
                </div>
            </div>

            <!-- 功能特性 -->
            <div class="bg-white rounded-lg shadow-lg p-6 border-2 border-[#0A0910]">
                <h2 class="text-2xl font-bold text-gray-800 mb-4">✨ 功能特性</h2>

                <div class="grid md:grid-cols-2 gap-4">
                    <div class="space-y-3">
                        <div class="flex items-center gap-2">
                            <span class="text-green-500">✅</span>
                            <span>实时配置更新</span>
                        </div>
                        <div class="flex items-center gap-2">
                            <span class="text-green-500">✅</span>
                            <span>本地持久化存储</span>
                        </div>
                        <div class="flex items-center gap-2">
                            <span class="text-green-500">✅</span>
                            <span>配置验证和测试</span>
                        </div>
                        <div class="flex items-center gap-2">
                            <span class="text-green-500">✅</span>
                            <span>友好的错误提示</span>
                        </div>
                    </div>

                    <div class="space-y-3">
                        <div class="flex items-center gap-2">
                            <span class="text-green-500">✅</span>
                            <span>支持多种AI模型</span>
                        </div>
                        <div class="flex items-center gap-2">
                            <span class="text-green-500">✅</span>
                            <span>分离文本和图片配置</span>
                        </div>
                        <div class="flex items-center gap-2">
                            <span class="text-green-500">✅</span>
                            <span>一键恢复默认设置</span>
                        </div>
                        <div class="flex items-center gap-2">
                            <span class="text-green-500">✅</span>
                            <span>向后兼容现有功能</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 技术说明 -->
            <div class="bg-white rounded-lg shadow-lg p-6 border-2 border-[#0A0910]">
                <h2 class="text-2xl font-bold text-gray-800 mb-4">🔧 技术实现</h2>

                <div class="space-y-4 text-gray-700">
                    <div>
                        <h3 class="font-semibold mb-2">配置管理</h3>
                        <p>使用Vue 3的响应式系统和localStorage实现配置的持久化存储和实时更新。</p>
                    </div>

                    <div>
                        <h3 class="font-semibold mb-2">API集成</h3>
                        <p>通过工具函数动态创建API客户端，确保所有请求都使用最新的配置。</p>
                    </div>

                    <div>
                        <h3 class="font-semibold mb-2">错误处理</h3>
                        <p>完善的错误处理机制，提供友好的用户提示和开发者调试信息。</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- 底部 -->
        <GlobalFooter />
    </div>
</template>

<script setup>
import GlobalNavigation from '@/components/GlobalNavigation.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'
import ConfigTest from '@/components/ConfigTest.vue'
</script>
