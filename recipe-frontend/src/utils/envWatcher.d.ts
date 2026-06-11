// 环境变量监听工具类型声明

export interface EnvSnapshot {
    [key: string]: string
}

export interface EnvCheckResult {
    hasChanged: boolean
    oldSnapshot?: EnvSnapshot
    newSnapshot?: EnvSnapshot
    snapshot?: EnvSnapshot
}

/**
 * 初始化环境变量监听
 * @returns 检查结果，包含是否有变化和快照信息
 */
export declare function initEnvWatcher(): EnvCheckResult

/**
 * 手动检查环境变量变化
 * @returns 检查结果，包含是否有变化和新旧快照
 */
export declare function checkEnvChanges(): EnvCheckResult

/**
 * 自动刷新环境变量配置
 * @returns 是否执行了刷新
 */
export declare function autoRefreshEnvSettings(): boolean
