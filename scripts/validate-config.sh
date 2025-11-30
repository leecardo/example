#!/bin/bash

# 项目配置验证脚本
# 验证Docker Compose配置和项目结构

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 打印带颜色的信息
info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 验证YAML语法
validate_yaml() {
    local file=$1
    if [[ -f $file ]]; then
        if python3 -c "import yaml; yaml.safe_load(open('$file'))" 2>/dev/null; then
            success "✓ $file YAML syntax is valid"
            return 0
        else
            error "✗ $file has invalid YAML syntax"
            return 1
        fi
    else
        error "✗ $file does not exist"
        return 1
    fi
}

# 验证目录结构
validate_directory_structure() {
    info "验证项目目录结构..."

    local required_dirs=(
        "cloud-service"
        "cloud-service/src"
        "ai-service"
        "ai-service/app"
        "docker"
        "docker/nginx"
        "docker/prometheus"
        "docker/mariadb/init"
    )

    local missing_dirs=()

    for dir in "${required_dirs[@]}"; do
        if [[ -d $dir ]]; then
            echo "✓ $dir"
        else
            echo "✗ $dir (missing)"
            missing_dirs+=("$dir")
        fi
    done

    if [[ ${#missing_dirs[@]} -eq 0 ]]; then
        success "所有必需的目录都存在"
        return 0
    else
        warning "缺少 ${#missing_dirs[@]} 个目录"
        return 1
    fi
}

# 验证必需文件
validate_required_files() {
    info "验证必需文件..."

    local required_files=(
        "cloud-service/pom.xml"
        "Dockerfile"
        ".gitignore"
        "run.sh"
        "docker-compose.yml"
        "docker-compose.dev.yml"
        ".env.example"
        "ai-service/requirements.txt"
        "ai-service/Dockerfile"
        "ai-service/main.py"
        "docker/nginx/nginx.conf"
        "docker/prometheus/prometheus.yml"
        "docker/mariadb/init/01-init.sql"
    )

    local missing_files=()

    for file in "${required_files[@]}"; do
        if [[ -f $file ]]; then
            echo "✓ $file"
        else
            echo "✗ $file (missing)"
            missing_files+=("$file")
        fi
    done

    if [[ ${#missing_files[@]} -eq 0 ]]; then
        success "所有必需的文件都存在"
        return 0
    else
        warning "缺少 ${#missing_files[@]} 个文件"
        return 1
    fi
}

# 验证环境变量配置
validate_env_config() {
    info "验证环境变量配置..."

    if [[ -f .env.example ]]; then
        local required_vars=(
            "DB_HOST"
            "DB_PORT"
            "DB_NAME"
            "REDIS_HOST"
            "CHROMADB_HOST"
            "EMBEDDING_MODEL"
            "NLP_MODEL"
        )

        local missing_vars=()

        for var in "${required_vars[@]}"; do
            if grep -q "$var" .env.example; then
                echo "✓ $var defined in .env.example"
            else
                echo "✗ $var not defined in .env.example"
                missing_vars+=("$var")
            fi
        done

        if [[ ${#missing_vars[@]} -eq 0 ]]; then
            success "所有环境变量都已定义"
            return 0
        else
            warning "缺少 ${#missing_vars[@]} 个环境变量定义"
            return 1
        fi
    else
        error ".env.example 文件不存在"
        return 1
    fi
}

# 验证Python依赖
validate_python_deps() {
    info "验证Python依赖配置..."

    if [[ -f ai-service/requirements.txt ]]; then
        local required_packages=(
            "fastapi"
            "uvicorn"
            "pydantic"
            "sentence-transformers"
            "transformers"
            "redis"
            "pymysql"
            "chromadb"
        )

        local missing_packages=()

        for package in "${required_packages[@]}"; do
            if grep -q "$package" ai-service/requirements.txt; then
                echo "✓ $package in requirements.txt"
            else
                echo "✗ $package not in requirements.txt"
                missing_packages+=("$package")
            fi
        done

        if [[ ${#missing_packages[@]} -eq 0 ]]; then
            success "所有Python依赖都已配置"
            return 0
        else
            warning "缺少 ${#missing_packages[@]} 个Python依赖"
            return 1
        fi
    else
        error "ai-service/requirements.txt 不存在"
        return 1
    fi
}

# 检查服务端口冲突
validate_ports() {
    info "检查端口配置..."

    local ports=(
        "8080:java-app"
        "8081:ai-service"
        "3306:mariadb"
        "6379:redis"
        "8000:chromadb"
        "5672:rabbitmq"
        "15672:rabbitmq-management"
    )

    for port_service in "${ports[@]}"; do
        local port=$(echo $port_service | cut -d: -f1)
        local service=$(echo $port_service | cut -d: -f2)

        if docker-compose -f docker-compose.yml config 2>/dev/null | grep -q "$port:$port"; then
            echo "✓ $service 使用端口 $port"
        else
            echo "? $service 端口 $port 配置未验证"
        fi
    done
}

# 主验证函数
main() {
    echo "=========================================="
    echo "      Example 项目配置验证"
    echo "=========================================="
    echo

    local exit_code=0

    # 验证YAML配置文件语法
    info "开始验证配置文件语法..."
    validate_yaml "docker-compose.yml" || exit_code=1
    validate_yaml "docker-compose.dev.yml" || exit_code=1
    echo

    # 验证目录结构
    validate_directory_structure || exit_code=1
    echo

    # 验证必需文件
    validate_required_files || exit_code=1
    echo

    # 验证环境变量配置
    validate_env_config || exit_code=1
    echo

    # 验证Python依赖
    validate_python_deps || exit_code=1
    echo

    # 检查端口配置
    validate_ports
    echo

    # 总结
    if [[ $exit_code -eq 0 ]]; then
        success "🎉 所有配置验证通过！项目可以正常启动。"
        echo
        info "下一步操作："
        echo "  1. 复制环境变量: cp .env.example .env"
        echo "  2. 启动开发环境: ./run.sh dev"
        echo "  3. 检查服务状态: ./run.sh status"
    else
        error "❌ 配置验证失败，请修复上述问题后重试。"
    fi

    echo "=========================================="
    exit $exit_code
}

# 检查依赖
if ! command -v python3 &> /dev/null; then
    error "Python3 未安装，无法进行YAML语法验证"
    exit 1
fi

# 运行主函数
main "$@"