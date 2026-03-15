#!/bin/bash

# EAM 项目构建和部署脚本

echo "========================================="
echo "EAM 企业资产管理系统 - 构建和部署"
echo "========================================="
echo ""

# 检查必要的工具
check_tool() {
    if ! command -v $1 &> /dev/null; then
        echo "错误: 未找到 $1"
        exit 1
    fi
}

echo "检查环境依赖..."
check_tool java
check_tool mvn
check_tool node
check_tool npm
echo "环境检查完成！"
echo ""

# 构建前端
echo "========================================="
echo "步骤 1/3: 构建前端项目"
echo "========================================="
cd frontend
if [ ! -d "node_modules" ]; then
    echo "正在安装前端依赖..."
    npm install
fi

echo "正在构建前端..."
npm run build

if [ $? -ne 0 ]; then
    echo "前端构建失败！"
    exit 1
fi

echo "前端构建完成！"
echo ""

# 构建后端
echo "========================================="
echo "步骤 2/3: 构建后端项目"
echo "========================================="
cd ../backend

echo "正在构建后端..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "后端构建失败！"
    exit 1
fi

echo "后端构建完成！"
echo ""

# 启动应用
echo "========================================="
echo "步骤 3/3: 启动应用"
echo "========================================="
echo "正在启动 Spring Boot 应用..."
java -jar target/eam-system-1.0.0.jar

echo ""
echo "========================================="
echo "部署完成！"
echo "访问地址: http://localhost:8080/eam"
echo "========================================="
