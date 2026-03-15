#!/bin/bash

# EAM 前端启动脚本

echo "正在启动 EAM 前端开发服务器..."

# 检查Node.js环境
if ! command -v node &> /dev/null; then
    echo "错误: 未找到 Node.js，请确保已安装 Node.js 18+"
    exit 1
fi

# 检查npm环境
if ! command -v npm &> /dev/null; then
    echo "错误: 未找到 npm"
    exit 1
fi

# 进入前端目录
cd "$(dirname "$0")"

# 检查node_modules是否存在
if [ ! -d "node_modules" ]; then
    echo "首次运行，正在安装依赖..."
    npm install
fi

# 启动开发服务器
echo "正在启动 Vite 开发服务器..."
npm run dev
