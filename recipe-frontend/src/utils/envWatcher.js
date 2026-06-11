// 环境变量监听工具

// 环境变量键名列表
const ENV_KEYS = [
    'VITE_TEXT_GENERATION_BASE_URL',
    'VITE_TEXT_GENERATION_API_KEY',
    'VITE_TEXT_GENERATION_MODEL',
    'VITE_TEXT_GENERATION_TEMPERATURE',
    'VITE_TEXT_GENERATION_TIMEOUT',
    'VITE_IMAGE_GENERATION_BASE_URL',
    'VITE_IMAGE_GENERATION_API_KEY',
    'VITE_IMAGE_GENERATION_MODEL'
]

// 获取当前环境变量快照
const getCurrentEnvSnapshot = () => {
    const snapshot = {}
    ENV_KEYS.forEach(key => {
        snapshot[key] = import.meta.env[key] || ''
    })
    return snapshot
}

// 比较环境变量是否有变化
const hasEnvChanged = (oldSnapshot, newSnapshot) => {
    if (!oldSnapshot) return false

    return ENV_KEYS.some(key => oldSnapshot[key] !== newSnapshot[key])
}

// 初始化环境变量监听
export const initEnvWatcher = () => {
    const currentSnapshot = getCurrentEnvSnapshot()

    // 在localStorage中存储环境变量快照
    const storageKey = 'yifan-fengshen-env-snapshot'
    const storedSnapshot = localStorage.getItem(storageKey)

    if (storedSnapshot) {
        try {
            const parsedSnapshot = JSON.parse(storedSnapshot)

            if (hasEnvChanged(parsedSnapshot, currentSnapshot)) {
                console.log('检测到环境变量变化')
                // 更新存储的快照
                localStorage.setItem(storageKey, JSON.stringify(currentSnapshot))
                return {
                    hasChanged: true,
                    oldSnapshot: parsedSnapshot,
                    newSnapshot: currentSnapshot
                }
            }
        } catch (error) {
            console.warn('解析环境变量快照失败:', error)
        }
    }

    // 更新存储的快照
    localStorage.setItem(storageKey, JSON.stringify(currentSnapshot))

    return {
        hasChanged: false,
        snapshot: currentSnapshot
    }
}

// 手动检查环境变量变化
export const checkEnvChanges = () => {
    const currentSnapshot = getCurrentEnvSnapshot()
    const storageKey = 'yifan-fengshen-env-snapshot'
    const storedSnapshot = localStorage.getItem(storageKey)

    let hasChanged = false
    let oldSnapshot = null

    if (storedSnapshot) {
        try {
            oldSnapshot = JSON.parse(storedSnapshot)
            hasChanged = hasEnvChanged(oldSnapshot, currentSnapshot)
        } catch (error) {
            console.warn('解析环境变量快照失败:', error)
        }
    }

    if (hasChanged) {
        // 更新存储的快照
        localStorage.setItem(storageKey, JSON.stringify(currentSnapshot))
    }

    return {
        hasChanged,
        oldSnapshot,
        newSnapshot: currentSnapshot
    }
}

// 自动刷新环境变量配置
export const autoRefreshEnvSettings = () => {
    const result = initEnvWatcher()

    if (result.hasChanged) {
        console.log('检测到环境变量变化，自动刷新配置')

        // 清除localStorage中的设置缓存，让应用重新读取环境变量
        localStorage.removeItem('yifan-fengshen-settings')

        // 自动刷新页面以应用最新配置
        window.location.reload()

        return true
    }

    return false
}