# 部署指南 / Deployment Guide

## 🚀 支持的部署平台

### Vercel 部署

-   **地址**: https://yffs.vercel.app/
-   **特点**: 自动部署、边缘网络、无服务器函数支持
-   **配置文件**: `vercel.json`

### Netlify 部署

-   **地址**: https://whattoeatai.netlify.app/
-   **特点**: 持续部署、表单处理、重定向规则
-   **配置文件**: `netlify.toml`

## 🔧 环境变量配置

部署前需要配置以下环境变量：

```env
# 文本生成 API（零一万物）
VITE_TEXT_GENERATION_BASE_URL=https://api.lingyiwanwu.com/v1/
VITE_TEXT_GENERATION_API_KEY=your_text_api_key_here
VITE_TEXT_GENERATION_MODEL=yi-lightning

# 图片生成 API（智谱 AI）
VITE_IMAGE_GENERATION_BASE_URL=https://open.bigmodel.cn/api/paas/v4/
VITE_IMAGE_GENERATION_API_KEY=your_image_api_key_here
VITE_IMAGE_GENERATION_MODEL=cogview-3-flash
```

## 📋 部署步骤

### Vercel 部署步骤

1. **Fork 项目**到你的 GitHub 账户
2. **登录 Vercel**并连接 GitHub
3. **导入项目**：选择 fork 的仓库
4. **配置环境变量**：在 Vercel 项目设置中添加上述环境变量
5. **部署**：Vercel 会自动构建和部署

或者使用一键部署：
[![Deploy with Vercel](https://vercel.com/button)](https://vercel.com/new/clone?repository-url=https://github.com/6TangYuan/what-to-eat&env=VITE_TEXT_GENERATION_BASE_URL,VITE_TEXT_GENERATION_API_KEY,VITE_TEXT_GENERATION_MODEL,VITE_IMAGE_GENERATION_BASE_URL,VITE_IMAGE_GENERATION_API_KEY,VITE_IMAGE_GENERATION_MODEL&envDescription=AI%20API%20配置)

### Netlify 部署步骤

1. **Fork 项目**到你的 GitHub 账户
2. **登录 Netlify**并连接 GitHub
3. **新建站点**：选择 fork 的仓库
4. **构建设置**：
    - Build command: `npm run build:netlify`
    - Publish directory: `dist`
    - Node version: `18`
5. **环境变量**：在 Netlify 站点设置中添加环境变量
6. **部署**：Netlify 会自动构建和部署

或者使用一键部署：
[![Deploy to Netlify](https://www.netlify.com/img/deploy/button.svg)](https://app.netlify.com/start/deploy?repository=https://github.com/6TangYuan/what-to-eat)

## 🔍 获取 Netlify Site ID

要获取 Netlify 部署状态徽章，需要找到你的 Site ID：

1. 登录 Netlify 控制台
2. 选择你的站点
3. 进入 **Site settings** > **General**
4. 在 **Site information** 中找到 **Site ID**
5. 将 README 中的 `your-site-id` 替换为实际的 Site ID

## 🛠️ 自定义域名

### Vercel 自定义域名

1. 在 Vercel 项目设置中选择 **Domains**
2. 添加你的域名
3. 按照提示配置 DNS 记录

### Netlify 自定义域名

1. 在 Netlify 站点设置中选择 **Domain management**
2. 添加自定义域名
3. 配置 DNS 记录或使用 Netlify DNS

## 🔄 自动部署

两个平台都支持 Git 集成的自动部署：

-   **推送到主分支**：自动触发生产环境部署
-   **推送到其他分支**：创建预览部署（Vercel）或分支部署（Netlify）
-   **Pull Request**：自动创建预览部署

## 📊 监控和分析

### Vercel Analytics

-   在项目设置中启用 Vercel Analytics
-   查看页面性能和用户访问数据

### Netlify Analytics

-   升级到付费计划以使用 Netlify Analytics
-   查看流量统计和性能指标

## 🚨 故障排除

### 常见问题

1. **构建失败**：检查环境变量是否正确配置
2. **页面 404**：确保 SPA 重定向规则已配置
3. **API 调用失败**：检查 API 密钥和端点配置
4. **静态资源加载失败**：检查构建输出目录配置

### 调试步骤

1. 查看构建日志
2. 检查环境变量配置
3. 本地测试构建命令
4. 检查网络和 API 连接

## 📞 支持

如果遇到部署问题，可以：

1. 查看项目 Issues
2. 参考官方文档：
    - [Vercel 文档](https://vercel.com/docs)
    - [Netlify 文档](https://docs.netlify.com/)
3. 提交新的 Issue 描述问题
