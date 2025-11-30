#!/bin/bash

# AI Service启动脚本

# 设置环境变量
export PYTHONPATH="${PYTHONPATH}:$(pwd)"

# 检查是否存在.env文件
if [ ! -f .env ]; then
    echo "Warning: .env file not found, using .env.example"
    cp .env.example .env
    echo "Please edit .env file with your configuration"
fi

# 创建必要的目录
mkdir -p temp_uploads
mkdir -p logs

# 检查Python版本
python_version=$(python3 --version 2>&1 | cut -d' ' -f2)
required_version="3.11"

if [ "$(printf '%s\n' "$required_version" "$python_version" | sort -V | head -n1)" != "$required_version" ]; then
    echo "Error: Python 3.11+ required, found $python_version"
    exit 1
fi

# 安装依赖（如果需要）
if [ "$1" = "--install" ]; then
    echo "Installing dependencies..."
    pip install -r requirements.txt
    shift
fi

# 启动服务
echo "Starting AI Service..."
python3 main.py "$@"