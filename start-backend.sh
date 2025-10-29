#!/bin/bash

# 企业级后台管理系统后端启动脚本

echo "🚀 正在启动企业级后台管理系统后端..."

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未找到Java环境，请先安装Java 8+"
    exit 1
fi

# 检查Maven环境
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误: 未找到Maven环境，请先安装Maven"
    exit 1
fi

# 进入后端目录
cd server

echo "📦 正在编译项目..."
mvn clean compile -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ 编译失败，请检查代码"
    exit 1
fi

echo "✅ 编译成功！"

# 检查配置文件
if [ ! -f "src/main/resources/application.yml" ]; then
    echo "❌ 错误: 未找到配置文件 application.yml"
    exit 1
fi

echo "🔧 配置信息:"
echo "   - 服务端口: 8080"
echo "   - 数据库: MySQL (localhost:3306)"
echo "   - 缓存: Redis (localhost:6379)"
echo "   - API文档: http://localhost:8080/doc.html"

echo ""
echo "⚠️  注意: 请确保MySQL和Redis服务已启动"
echo ""

# 启动应用
echo "🚀 正在启动应用..."
mvn spring-boot:run

echo "👋 应用已停止"
