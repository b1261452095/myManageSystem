#!/bin/bash

echo "正在启动企业级后台管理系统前端..."

cd web

# 检查 node_modules 是否存在
if [ ! -d "node_modules" ]; then
    echo "首次运行，正在安装依赖..."
    npm install
fi

echo "启动开发服务器..."
npm run dev

