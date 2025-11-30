#!/bin/bash

# 项目运行脚本 - 支持Java和Python服务
set -e

# 颜色定義
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 函数：打印带颜色的信息
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

# 显示帮助信息
show_help() {
    echo "Example Project - Java + Python AI Services"
    echo ""
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo ""
    echo "Commands:"
    echo "  dev              Start development environment"
    echo "  prod             Start production environment"
    echo "  build            Build all services"
    echo "  clean            Clean up containers and volumes"
    echo "  logs             Show service logs"
    echo "  status           Show service status"
    echo "  test-java        Test Java Spring Boot application"
    echo "  test-python      Test Python AI service"
    echo "  setup            Setup development environment"
    echo "  help             Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 dev           # Start development environment"
    echo "  $0 prod          # Start production environment"
    echo "  $0 logs java     # Show Java application logs"
    echo "  $0 clean         # Clean everything"
}

# 检查Docker和Docker Compose
check_dependencies() {
    if ! command -v docker &> /dev/null; then
        error "Docker is not installed. Please install Docker first."
        exit 1
    fi

    if ! command -v docker-compose &> /dev/null; then
        error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi

    success "Dependencies check passed"
}

# 设置开发环境
setup_environment() {
    info "Setting up development environment..."

    # 复制环境配置文件
    if [ ! -f ai-service/.env ]; then
        info "Creating AI service environment file..."
        cp ai-service/.env.example ai-service/.env
        success "Created ai-service/.env file"
    else
        warning "ai-service/.env already exists"
    fi

    # 创建必要的目录
    mkdir -p docker/{nginx,prometheus,mariadb/init}

    success "Development environment setup completed"
}

# 构建服务
build_services() {
    info "Building all services..."

    # 构建Java应用
    info "Building Java Spring Boot application..."
    mvn clean package -DskipTests

    # 构建Docker镜像
    info "Building Docker images..."
    docker-compose build

    success "All services built successfully"
}

# 启动开发环境
start_dev() {
    info "Starting development environment..."
    check_dependencies

    # 使用开发配置启动
    docker-compose -f docker-compose.dev.yml up -d

    info "Waiting for services to be ready..."
    sleep 10

    # 检查服务状态
    check_service_status

    success "Development environment started successfully!"
    info ""
    info "Service URLs:"
    info "  Java Spring Boot: http://localhost:8080"
    info "  Python AI Service: http://localhost:8081"
    info "  MariaDB: localhost:3306"
    info "  Redis: localhost:6379"
    info "  ChromaDB: http://localhost:8000"
}

# 启动生产环境
start_prod() {
    info "Starting production environment..."
    check_dependencies

    docker-compose up -d

    info "Waiting for services to be ready..."
    sleep 15

    check_service_status

    success "Production environment started successfully!"
    info ""
    info "Service URLs:"
    info "  Main Application: http://localhost (Nginx)"
    info "  Java Spring Boot: http://localhost:8080"
    info "  Python AI Service: http://localhost:8081"
    info "  RabbitMQ Management: http://localhost:15672"
    if [ "$1" = "monitoring" ]; then
        info "  Prometheus: http://localhost:9090"
        info "  Grafana: http://localhost:3000"
    fi
}

# 检查服务状态
check_service_status() {
    info "Checking service status..."

    services=("java-app:8080" "ai-service:8081" "mariadb:3306" "redis:6379" "chromadb:8000")
    all_healthy=true

    for service in "${services[@]}"; do
        IFS=':' read -r name port <<< "$service"
        if nc -z localhost "$port" 2>/dev/null; then
            success "$name is running on port $port"
        else
            error "$name is not accessible on port $port"
            all_healthy=false
        fi
    done

    if [ "$all_healthy" = true ]; then
        success "All services are healthy"
    else
        warning "Some services may still be starting up..."
    fi
}

# 显示日志
show_logs() {
    local service=$1
    if [ -z "$service" ]; then
        info "Showing all service logs..."
        docker-compose logs -f
    else
        info "Showing logs for service: $service"
        docker-compose logs -f "$service"
    fi
}

# 清理环境
clean_environment() {
    info "Cleaning up environment..."

    # 停止服务
    docker-compose down -v 2>/dev/null || docker-compose -f docker-compose.dev.yml down -v

    # 删除相关镜像（可选）
    read -p "Do you want to delete Docker images? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        docker system prune -f
        success "Docker images cleaned up"
    fi

    success "Environment cleaned up"
}

# 显示状态
show_status() {
    info "Service Status:"
    docker-compose ps

    echo ""
    info "Health Checks:"
    check_service_status
}

# 测试Java应用
test_java() {
    info "Testing Java Spring Boot application..."

    # 检查应用是否运行
    if curl -f http://localhost:8080/actuator/health >/dev/null 2>&1; then
        success "Java application is healthy"

        # 运行单元测试
        info "Running unit tests..."
        mvn test

        success "Java tests completed"
    else
        error "Java application is not running or unhealthy"
        exit 1
    fi
}

# 测试Python服务
test_python() {
    info "Testing Python AI Service..."

    # 检查服务是否运行
    if curl -f http://localhost:8081/health >/dev/null 2>&1; then
        success "Python AI service is healthy"

        # 运行Python测试
        info "Running Python tests..."
        docker-compose exec ai-service python -m pytest tests/ -v

        success "Python tests completed"
    else
        error "Python AI service is not running or unhealthy"
        exit 1
    fi
}

# 主逻辑
case "${1:-help}" in
    "dev")
        start_dev
        ;;
    "prod")
        if [ "$2" = "monitoring" ]; then
            docker-compose --profile monitoring up -d
        else
            start_prod
        fi
        ;;
    "build")
        build_services
        ;;
    "clean")
        clean_environment
        ;;
    "logs")
        show_logs "$2"
        ;;
    "status")
        show_status
        ;;
    "test-java")
        test_java
        ;;
    "test-python")
        test_python
        ;;
    "setup")
        setup_environment
        ;;
    "help"|*)
        show_help
        ;;
esac