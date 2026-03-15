#!/bin/bash

# EAM 后端启动脚本

echo "正在启动 EAM 后端服务..."

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到 Java，请确保已安装 JDK 17+"
    exit 1
fi

# 检查Maven环境
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到 Maven，请确保已安装 Maven"
    exit 1
fi

# 进入后端目录
cd "$(dirname "$0")"

# 启动服务
echo "正在使用 Maven 启动 Spring Boot 应用..."
mvn spring-boot:run
