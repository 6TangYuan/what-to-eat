# 料理占卜师功能设计文档

## 概述

料理占卜师是一个结合占卜元素与美食推荐的娱乐化功能，通过AI生成有趣的菜品推荐和占卜解析，为用户提供独特的美食体验。

## 架构设计

### 技术栈
- Vue 3 + TypeScript
- Tailwind CSS（神秘主题色彩）
- 复用现有的AI服务（智谱AI）
- 本地存储（localStorage）

### 页面结构
```
FortuneCooking.vue (主页面)
├── 占卜师角色展示区域
├── 占卜类型选择区域
├── 占卜参数配置区域
├── 占卜过程动画区域
├── 占卜结果展示区域
└── 历史记录区域
```

## 组件设计

### 核心组件
1. **FortuneCooking.vue** - 主页面组件
2. **FortuneCard.vue** - 占卜结果卡片组件
3. **ZodiacSelector.vue** - 星座生肖选择器
4. **MoodSelector.vue** - 心情选择器
5. **FortuneAnimation.vue** - 占卜过程动画
6. **FortuneHistory.vue** - 历史记录组件

## 数据模型

### 占卜类型定义
```typescript
type FortuneType = 'daily' | 'mood' | 'couple' | 'number'

interface FortuneRequest {
  type: FortuneType
  params: DailyFortuneParams | MoodFortuneParams | CoupleFortuneParams | NumberFortuneParams
}

interface DailyFortuneParams {
  zodiac: string // 星座
  animal: string // 生肖
  date: string
}

interface MoodFortuneParams {
  moods: string[] // 心情状态数组
  intensity: number // 情绪强度 1-5
}

interface CoupleFortuneParams {
  user1: PersonInfo
  user2: PersonInfo
}

interface NumberFortuneParams {
  number: number // 1-99
  isRandom: boolean
}

interface FortuneResult {
  id: string
  type: FortuneType
  date: string
  dishName: string
  reason: string
  luckyIndex: number // 1-10
  description: string
  tips: string[]
  difficulty: 'easy' | 'medium' | 'hard'
  cookingTime: number
  mysticalMessage: string // 神秘话语
}
```

### 占卜师角色配置
```typescript
interface FortuneTeller {
  name: string
  avatar: string
  greetings: string[]
  phrases: {
    [key in FortuneType]: {
      opening: string[]
      closing: string[]
      processing: string[]
    }
  }
  mysticalWords: string[]
}
```

## AI服务扩展

### 新增AI服务方法
1. `generateDailyFortune(params: DailyFortuneParams)` - 生成今日运势菜
2. `generateMoodCooking(params: MoodFortuneParams)` - 生成心情料理
3. `generateCoupleCooking(params: CoupleFortuneParams)` - 生成缘分配菜
4. `generateNumberFortune(params: NumberFortuneParams)` - 生成幸运数字菜

## 用户界面设计

### 色彩方案
- 主色调：深紫色、神秘蓝、金色（营造神秘感）
- 渐变背景：星空、水晶球、塔罗牌风格
- 保持与现有平台的视觉协调

### 交互流程
1. 用户进入页面 → 占卜师欢迎语
2. 选择占卜类型 → 显示对应参数配置
3. 配置参数 → 开始占卜动画
4. AI生成结果 → 展示占卜结果
5. 支持分享、保存、重新占卜

### 动画效果
- 水晶球旋转效果
- 卡牌翻转动画
- 星座运行轨迹
- 数字滚动效果
- 结果揭晓的神秘感

## 占卜逻辑设计

### 今日运势菜逻辑
```typescript
// 基于星座特性 + 生肖属性 + 日期因子
const getDailyFortune = (zodiac: string, animal: string, date: string) => {
  const zodiacTraits = getZodiacTraits(zodiac) // 星座特性
  const animalTraits = getAnimalTraits(animal) // 生肖属性
  const dateEnergy = getDateEnergy(date) // 日期能量
  
  return combineFortuneFactors(zodiacTraits, animalTraits, dateEnergy)
}
```

### 心情料理逻辑
```typescript
const moodCookingMap = {
  happy: ['甜品类', '色彩丰富', '庆祝菜品'],
  sad: ['温暖汤品', '治愈系', '家常菜'],
  anxious: ['清淡菜品', '舒缓茶饮', '简单制作'],
  tired: ['营养补充', '快手菜', '能量食物'],
  // ...
}
```

## 数据存储

### 本地存储结构
```typescript
interface FortuneStorage {
  history: FortuneResult[]
  userPreferences: {
    defaultZodiac?: string
    defaultAnimal?: string
    favoriteFortuneType?: FortuneType
  }
  feedback: {
    [fortuneId: string]: 'accurate' | 'inaccurate'
  }
}
```

## 分享功能设计

### 分享卡片内容
- 占卜类型和日期
- 推荐菜品和幸运指数
- 神秘解析文案
- 平台品牌水印
- 邀请朋友体验的文案

## 性能优化

### 缓存策略
- 同一天同一用户的运势菜结果缓存
- 常用星座生肖组合预计算
- 占卜师话语本地缓存

### 动画优化
- 使用CSS3动画减少JS计算
- 懒加载复杂动画效果
- 移动端简化动画

## 扩展性考虑

### 未来功能扩展
- 节日特殊占卜（春节、情人节等）
- 占卜师角色升级和解锁
- 用户自定义占卜参数
- 社交分享和比较功能
- 占卜准确度统计和改进