#!/bin/bash

# 安装依赖
npm install

# 构建项目（跳过类型检查）
npm run build:netlify

echo "Build completed successfully!"