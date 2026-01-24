# CLAUDE.md

本文件为Claude Code (claude.ai/code)在处理此代码库时提供指导。

## 项目概览

这是一个多语言微服务项目，包含：
- **Java Spring Boot 4.0.2** 应用：主要业务逻辑、支付系统、AI集成（LangChain4j）
- **Python FastAPI AI服务**：自然语言处理、文档分析、向量搜索
- 项目使用微服务架构，支持容器化部署

## 技术架构

### Java Spring Boot应用（端口：8080）
- **Spring Boot 4.0.2** 配合 Java 25 LTS（虚拟线程、现代特性）
- **Spring Cloud 2025.1.0** + **Spring Cloud Alibaba 2025.0.0.0**
- **AI集成**: LangChain4j 1.10.0（OpenAI、Ollama）
- **支付系统**: 支付宝集成 + 重试机制
- **数据库**: MariaDB 3.5.7 + JPA + MyBatis Plus 3.5.16
- **缓存**: Redis + Lettuce + Redisson 4.1.0
- **消息队列**: RocketMQ 2.3.5 + RabbitMQ
- **文件处理**: Apache Tika 3.1.0、PDFBox 3.0.4、Apache POI 5.4.1
- **Web服务器**: Tomcat（Spring Boot 4.0 默认）

### Python AI服务（端口：8081）
- **Web框架**: FastAPI + Uvicorn（异步高性能）
- **AI模型**: sentence-transformers + Hugging Face transformers
- **文档处理**: PyPDF2、python-docx
- **向量数据库**: ChromaDB
- **数据库连接**: Redis + PyMySQL
- **异步支持**: asyncio、aiohttp

## 项目结构

```
example/
├── src/                              # Java Spring Boot应用
│   └── main/java/com/example/test/    # Java源代码
│       ├── ai/                       # AI集成（LangChain4j）
│       ├── paymentservice/           # 支付系统
│       ├── config/                   # Spring配置
│       └── ...                       # 其他业务模块
├── ai-service/                       # Python FastAPI AI服务
│   ├── app/
│   │   ├── api/v1/                   # API路由端点
│   │   ├── models/                   # AI模型管理
│   │   ├── services/                 # 业务逻辑服务
│   │   └── utils/                    # 工具和配置
│   ├── tests/                        # Python测试
│   └── requirements.txt              # Python依赖
├── docker-compose.yml                # 生产环境配置
├── docker-compose.dev.yml            # 开发环境配置
└── run.sh                           # 统一启动脚本
```

## 构建和开发命令

### Java应用
```bash
# 激活 Java 25 环境（使用 SDKMAN）
source ~/.sdkman/bin/sdkman-init.sh

# 构建项目
mvn clean compile

# 运行测试
mvn test

# 本地运行
mvn spring-boot:run

# Docker构建
docker build -t java-app .
```

### Python AI服务
```bash
cd ai-service

# 安装依赖
pip install -r requirements.txt

# 本地运行
python main.py

# 运行测试
pytest tests/

# Docker构建
docker build -t ai-service .
```

### 容器化部署
```bash
# 开发环境
./run.sh dev

# 生产环境
./run.sh prod

# 查看服务状态
./run.sh status

# 查看日志
./run.sh logs
```

## 服务间通信

### Java调用Python AI服务示例
```java
@FeignClient(name = "ai-service", url = "${ai.service.url:http://localhost:8081}")
public interface AIServiceClient {
    @PostMapping("/api/v1/ai/embedding")
    EmbeddingResponse createEmbedding(@RequestBody EmbeddingRequest request);
}
```

### 共享基础设施
- **数据库**: MariaDB（127.0.0.1:3306）
- **缓存**: Redis（122.51.168.11:6379）
- **向量数据库**: ChromaDB（localhost:8000）
- **消息队列**: RabbitMQ（122.51.168.11:5672）

## AI能力对比

| 功能 | Java集成 | Python服务 |
|------|----------|------------|
| 文本嵌入 | LangChain4j 1.10.0（高级） | sentence-transformers（高级） |
| 文档处理 | Apache Tika 3.1.0 | PyPDF2 + python-docx |
| 向量搜索 | ChromaDB Java客户端 | ChromaDB + 自定义处理 |
| NLP任务 | LangChain4j | 完整Hugging Face生态 |
| 异步处理 | 虚拟线程（Java 25） | FastAPI（原生） |

## 开发工作流

1. **本地开发**：使用`./run.sh dev`启动完整环境
2. **Java开发**：主要业务逻辑、数据库操作、支付系统
3. **Python开发**：AI模型处理、文档分析、向量搜索
4. **API集成**：通过Feign客户端进行服务间调用
5. **测试**：`mvn test` + `pytest tests/`

## 配置管理

### Java application.yml
- 数据库连接（本地+远程Redis）
- AI模型配置（LangChain4j）
- 支付服务参数

### Python .env
- 模型路径配置
- 数据库连接
- 日志级别设置

## 监控和健康检查

- **Java应用**: `http://localhost:8080/actuator/health`
- **Python服务**: `http://localhost:8081/health`
- **RabbitMQ管理**: `http://localhost:15672`
- **ChromaDB**: `http://localhost:8000`

## 测试策略

### 集成测试
- TestContainers支持（仅Java）
- pytest支持（Python）
- 服务间API测试

### 性能优化
- Java: 虚拟线程 + 连接池优化
- Python: 异步处理 + 模型复用
- 共享缓存策略

## 故障排查

### 常见问题
1. **端口冲突**: 确保8080/8081端口可用
2. **模型加载**: Python首次启动需下载模型文件
3. **数据库连接**: 检查MariaDB和Redis连通性
4. **CORS配置**: 跨域请求已配置但需验证

### 日志查看
```bash
# 查看所有服务日志
./run.sh logs

# 查看特定服务日志
./run.sh logs java
./run.sh logs ai-service
```

## 扩展建议

1. **API网关**: 考虑引入Spring Cloud Gateway
2. **服务发现**: 添加Eureka或Consul
3. **监控**: 集成Prometheus + Grafana
4. **安全**: 添加JWT认证和权限控制

## 版本历史

### 2026-01-24 升级
- Java: 21 → **25 LTS** (OpenJDK Temurin)
- Spring Boot: 3.4.2 → **4.0.2**
- Spring Cloud: 2024.0.0 → **2025.1.0**
- Spring Cloud Alibaba: 2023.0.3.2 → **2025.0.0.0**
- LangChain4j: 1.0.0-rc1 → **1.10.0**
- MyBatis Plus: 3.5.5 → **3.5.16**
- Redisson: 3.16.3 → **4.1.0**
- Web服务器: Undertow → **Tomcat**（Spring Boot 4.0 移除了 Undertow 支持）
- 命名空间迁移: `javax.annotation` → `jakarta.annotation`