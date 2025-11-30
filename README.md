# Example Project - Java + Python AI Services

一个基于Spring Boot 3.4.2 + Python FastAPI的现代微服务项目，展示了多语言技术在AI和数据处理领域的协作。

## 🏗️ 项目架构

```
example/
├── cloud-service/                 # Java Spring Boot应用
│   ├── src/
│   │   └── main/java/com/example/test/
│   │       ├── ai/             # AI集成（LangChain4j）
│   │       ├── paymentservice/ # 支付服务
│   │       ├── config/         # 配置类
│   │       └── ...             # 其他业务模块
│   ├── target/                 # Maven构建输出
│   └── pom.xml                 # Maven配置
├── ai-service/                 # Python FastAPI AI服务
│   ├── app/
│   │   ├── api/v1/            # API路由
│   │   ├── models/            # AI模型管理
│   │   ├── services/          # 业务逻辑
│   │   └── utils/             # 工具组件
│   ├── tests/                  # Python测试
│   ├── scripts/                # 脚本工具
│   └── requirements.txt        # Python依赖
├── docker/                     # Docker配置文件
│   ├── nginx/                  # Nginx反向代理
│   ├── prometheus/             # Prometheus监控
│   └── mariadb/init/           # 数据库初始化
├── config/                     # 项目配置目录
├── docker-compose.yml          # 生产环境
├── docker-compose.dev.yml      # 开发环境
├── Dockerfile                  # Java应用容器化
├── .env.example               # 环境变量模板
├── .gitignore                 # Git忽略规则
├── run.sh                     # 项目启动脚本
└── README.md                  # 项目文档
```

## 🚀 核心特性

### Java Spring Boot应用
- ✨ **Spring Boot 3.4.2** + **Java 21** (虚拟线程、现代语法)
- 🤖 **LangChain4j 1.0.0-rc1** AI集成 (OpenAI、Ollama)
- 💳 **支付系统** (支付宝集成 + 重试机制)
- 📊 **文档处理** (PDF、Word、Excel)
- 🗄️ **多数据库支持** (JPA + MyBatis Plus)
- 🚀 **高性能中间件** (Redis + RabbitMQ + RocketMQ)

### Python AI服务
- 🧠 **文本嵌入** (sentence-transformers)
- 📝 **文本摘要** (Hugging Face transformers)
- 🔍 **关键词提取** (NLP处理)
- 📚 **文档分析** (PDF、DOCX处理)
- 🔎 **向量搜索** (ChromaDB集成)
- ⚡ **异步处理** (FastAPI)

## 🛠️ 技术栈

### 后端服务
| 服务 | 语言 | 框架 | 端口 |
|------|------|------|------|
| Java应用 | Java 21 | Spring Boot 3.4.2 | 8080 |
| AI服务 | Python 3.11 | FastAPI | 8081 |

### 基础设施
- **数据库**: MariaDB 10.11
- **缓存**: Redis 7
- **向量数据库**: ChromaDB
- **消息队列**: RabbitMQ 3
- **Web服务器**: Nginx (生产环境)

## 📦 快速开始

### 1. 环境要求
- Docker & Docker Compose
- Java 21+
- Python 3.11+
- Maven 3.8+

### 2. 一键启动开发环境
```bash
# 克隆项目
git clone <repository-url>
cd example

# 设置环境和配置文件
./run.sh setup

# 复制并编辑环境变量（可选）
cp .env.example .env
# 编辑 .env 文件根据需要修改配置

# 启动开发环境
./run.sh dev
```

### 3. 验证服务
```bash
# 检查所有服务状态
./run.sh status

# Java应用健康检查
curl http://localhost:8080/actuator/health

# Python AI服务健康检查
curl http://localhost:8081/health
```

## 🔧 开发指南

### Java应用开发
```bash
# 进入Java项目目录
cd cloud-service

# 编译构建
mvn clean compile

# 运行测试
mvn test

# 本地运行
mvn spring-boot:run

# Docker构建（在根目录）
cd ..
docker build -t java-app -f Dockerfile .
```

### Python服务开发
```bash
cd ai-service

# 安装依赖
pip install -r requirements.txt

# 本地运行
python main.py

# 运行测试
pytest tests/
```

## 🌐 API示例

### Java应用调用Python AI服务
```java
@FeignClient(name = "ai-service", url = "http://localhost:8081")
public interface AIServiceClient {

    @PostMapping("/api/v1/ai/embedding")
    EmbeddingResponse createEmbedding(@RequestBody EmbeddingRequest request);

    @PostMapping("/api/v1/nlp/summarize")
    SummaryResponse summarizeText(@RequestBody SummaryRequest request);
}
```

### 使用示例
```bash
# 文本嵌入
curl -X POST "http://localhost:8081/api/v1/ai/embedding" \
     -H "Content-Type: application/json" \
     -d '{"text": "Hello, world!"}'

# 文本摘要
curl -X POST "http://localhost:8081/api/v1/nlp/summarize" \
     -H "Content-Type: application/json" \
     -d '{"text": "长文本内容...", "max_length": 100}'

# 文档上传处理
curl -X POST "http://localhost:8081/api/v1/document/upload" \
     -F "file=@document.pdf" \
     -F "collection_name=docs"
```

## 🎯 使用场景

1. **AI能力增强**: 为Java应用提供Python丰富的AI生态
2. **文档处理**: 智能文档分析、摘要和检索
3. **微服务架构**: 多语言协同的微服务实践
4. **RAG系统**: 构建检索增强生成应用
5. **支付系统**: 完整的支付处理方案

## 📊 监控和运维

### 查看日志
```bash
# 所有服务日志
./run.sh logs

# 特定服务日志
./run.sh logs java
./run.sh logs ai-service
```

### 健康检查
```bash
# 测试Java应用
./run.sh test-java

# 测试Python服务
./run.sh test-python
```

### 生产环境部署
```bash
# 启动生产环境
./run.sh prod

# 启动生产环境 + 监控
./run.sh prod monitoring
```

## 🔐 配置说明

### 环境变量配置

项目提供了完整的环境变量模板 `.env.example`，包含以下主要配置：

| 变量类别 | 变量名 | 描述 | 默认值 |
|---------|-------|------|--------|
| **服务端口** | `JAVA_APP_PORT` | Java应用端口 | 8080 |
| | `PYTHON_AI_SERVICE_PORT` | Python AI服务端口 | 8081 |
| | `NGINX_PORT` | Nginx代理端口 | 80 |
| **数据库** | `DB_HOST` | MariaDB主机 | mariadb |
| | `DB_PORT` | 数据库端口 | 3306 |
| | `DB_NAME` | 数据库名称 | test |
| | `DB_USER` | 数据库用户 | root |
| | `DB_PASSWORD` | 数据库密码 | root123 |
| **缓存** | `REDIS_HOST` | Redis主机 | redis |
| | `REDIS_PORT` | Redis端口 | 6379 |
| | `REDIS_DB` | Redis数据库 | 0 |
| **AI服务** | `EMBEDDING_MODEL` | 嵌入模型 | sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2 |
| | `NLP_MODEL` | NLP处理模型 | facebook/bart-large-cnn |
| | `PYTHON_DEBUG` | Python调试模式 | false |
| **向量数据库** | `CHROMADB_HOST` | ChromaDB主机 | chromadb |
| | `CHROMADB_PORT` | ChromaDB端口 | 8000 |
| **消息队列** | `RABBITMQ_HOST` | RabbitMQ主机 | rabbitmq |
| | `RABBITMQ_PORT` | RabbitMQ端口 | 5672 |

### 配置文件位置

- **Java配置**: `cloud-service/src/main/resources/application.yml`
- **Python配置**: `ai-service/.env` 和 `ai-service/app/utils/config.py`
- **Docker配置**: `docker/` 目录下的各种配置文件
- **Nginx配置**: `docker/nginx/nginx.conf`
- **Prometheus配置**: `docker/prometheus/prometheus.yml`

### 服务端口映射

| 服务 | 内部端口 | 外部端口 | 说明 |
|------|----------|----------|------|
| Java应用 | 8080 | 8080 | Spring Boot应用 |
| Python AI服务 | 8081 | 8081 | FastAPI服务 |
| MariaDB | 3306 | 3306 | 数据库 |
| Redis | 6379 | 6379 | 缓存 |
| ChromaDB | 8000 | 8000 | 向量数据库 |
| RabbitMQ | 5672 | 5672 | 消息队列 |
| RabbitMQ管理 | 15672 | 15672 | 管理界面 |
| Prometheus | 9090 | 9090 | 监控 |
| Grafana | 3000 | 3000 | 可视化 |

## 🧪 测试

### 集成测试
```bash
# 运行所有测试
./run.sh test-java && ./run.sh test-python

# 性能测试
mvn verify -Pperformance
```

### API测试
```bash
# 文档处理测试
curl -X POST "http://localhost:8081/api/v1/document/upload" \
     -F "file=@test.pdf"

# 向量搜索测试
curl -X POST "http://localhost:8081/api/v1/document/search" \
     -H "Content-Type: application/json" \
     -d '{"query": "搜索关键词"}'
```

## 🔧 故障排查

### 常见问题

1. **端口冲突**
   ```bash
   # 检查端口占用
   netstat -tlnp | grep :8080
   # 修改docker-compose.dev.yml中的端口映射
   ```

2. **AI模型下载失败**
   ```bash
   # 检查Python服务日志
   ./run.sh logs ai-service
   # 手动下载模型（在ai-service目录下）
   python -c "from sentence_transformers import SentenceTransformer; SentenceTransformer('sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2')"
   ```

3. **数据库连接失败**
   ```bash
   # 检查MariaDB容器状态
   docker-compose ps mariadb
   # 测试连接
   mysql -h127.0.0.1 -uroot -proot123 -e "SHOW DATABASES;"
   ```

4. **Python环境问题**
   ```bash
   # 进入Python容器
   docker-compose exec ai-service bash
   # 检查依赖
   pip list
   # 重新安装依赖
   pip install -r requirements.txt
   ```

5. **服务间通信问题**
   ```bash
   # 检查网络连接
   docker-compose exec java_app ping ai-service
   # 检查CORS配置
   curl -H "Origin: http://localhost:8080" http://localhost:8081/health
   ```

### 日志查看

```bash
# 查看所有服务日志
./run.sh logs

# 查看特定服务日志
./run.sh logs java
./run.sh logs ai-service
./run.sh logs mariadb

# 实时跟踪日志
docker-compose logs -f java-app
docker-compose logs -f ai-service
```

### 性能监控

```bash
# 检查资源使用情况
docker stats

# 检查容器状态
docker-compose ps

# 进入容器调试
docker-compose exec java-app bash
docker-compose exec ai-service bash
```

## 📈 性能优化

### Java应用优化
- **虚拟线程**: 使用Java 21虚拟线程提高并发性能
- **连接池优化**: HikariCP配置优化，复用数据库连接
- **缓存策略**: Redis缓存热点数据，减少数据库查询
- **异步处理**: WebFlux异步处理，提高吞吐量
- **Undertow服务器**: 替代Tomcat，降低资源占用

### Python服务优化
- **异步处理**: FastAPI原生async/await支持
- **模型懒加载**: 按需加载AI模型，节省内存
- **模型复用**: 多次请求复用已加载模型
- **批量处理**: 支持批量文本处理，提高效率
- **Redis缓存**: 缓存嵌入向量和处理结果

### 系统级优化
- **容器资源限制**: 合理配置memory和CPU限制
- **网络优化**: Docker network配置优化
- **存储优化**: 数据库和Redis持久化优化
- **监控告警**: Prometheus+Grafana监控体系

## 🛡️ 安全考虑

- **CORS配置**: 跨域请求严格控制
- **API文档隐藏**: 生产环境隐藏Swagger文档
- **环境变量**: 敏感配置通过环境变量管理
- **数据库安全**: 连接加密和权限控制
- **服务通信**: 内部服务通信安全
- **容器安全**: 非root用户运行容器
- **日志安全**: 避免敏感信息泄露到日志

## 🤝 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 📝 更新日志

### v1.1.0 (2024-11-30)
- ✨ 添加Python AI服务 (FastAPI + Hugging Face)
- ✨ 完整的Docker容器化部署方案
- ✨ 多语言微服务架构实现
- ✨ 文档处理和向量搜索功能
- ✨ 项目结构优化: src/ → cloud-service/
- ✨ 完善的环境配置和故障排查文档

### v1.0.0 (2024-01-01)
- ✨ Spring Boot 3.4.2 + Java 21 基础架构
- ✨ LangChain4j AI集成
- ✨ 支付系统实现
- ✨ 中间件集成 (Redis + RabbitMQ + RocketMQ)
- ✨ 现代Java特性演示

---

## 📚 学习资源

### 技术文档
1. **Spring Boot 3.4** - [官方文档](https://docs.spring.io/spring-boot/docs/3.4.2/reference/html/)
2. **Java 21 新特性** - [Release Notes](https://openjdk.org/projects/jdk/21/)
3. **FastAPI文档** - [官方教程](https://fastapi.tiangolo.com/tutorial/)
4. **LangChain4j** - [GitHub仓库](https://github.com/langchain4j/langchain4j)

### 项目相关
1. **面试问题demo** - `cloud-service/src/main/java/com/example/test/demo/`
2. **JDK 11-21新特性** - Lambda表达式、虚拟线程、模式匹配
3. **中间件学习** - Redis缓存、RabbitMQ消息队列、RocketMQ
4. **LangChain4j实践** - AI模型集成、RAG系统实现
5. **微服务架构** - Spring Cloud + Docker Compose最佳实践

### AI和机器学习
1. **Hugging Face** - [Transformers库](https://huggingface.co/docs/transformers/)
2. **Sentence Transformers** - [Embedding模型](https://sbert.net/)
3. **ChromaDB** - [向量数据库](https://docs.trychroma.com/)
4. **Python NLP** - 自然语言处理实践

### 运维和部署
1. **Docker Compose** - [多容器应用](https://docs.docker.com/compose/)
2. **Prometheus监控** - [时序数据库](https://prometheus.io/docs/)
3. **Nginx配置** - [反向代理](https://nginx.org/en/docs/)
4. **容器化最佳实践** - 多阶段构建、安全配置

---

## 📞 支持与反馈

- 🐛 **Bug报告**: 请在 [Issues](https://github.com/example/project/issues) 中提交
- 💡 **功能建议**: 欢迎在 [Discussions](https://github.com/example/project/discussions) 中讨论
- 📧 **技术交流**: 欢迎提交 Pull Request

---

**⭐ 如果这个项目对你有帮助，请给个Star！**

**🚀 关注我们获取更多技术实践和更新！**

