# CLAUDE.md

本文件为Claude Code (claude.ai/code)在处理此代码库时提供指导。

## 项目概览

这是一个基于Spring Boot 3.4.2的Java 21应用，展示了各种Java特性、AI集成（使用LangChain4j）、支付处理系统和中间件实现。项目使用Undertow作为Web服务器而不是Tomcat。

## 关键技术和依赖

- **Spring Boot 3.4.2** 配合 Java 21
- **LangChain4j 1.0.0-rc1** 用于AI/LLM集成（OpenAI、Ollama）
- **数据库**: MariaDB 配合 JPA 和 MyBatis Plus
- **缓存**: Redis 配合 Lettuce 客户端和 Redisson
- **消息队列**: RocketMQ 和 RabbitMQ
- **文件处理**: Apache Tika、PDFBox、Apache POI 用于文档解析
- **HTTP客户端**: OkHttp 和 Retrofit
- **机器学习库**: ND4J 用于数值计算
- **测试**: JUnit Jupiter 和 TestContainers

## 构建和开发命令

```bash
# 构建项目
mvn clean compile

# 运行测试
mvn test

# 打包应用
mvn clean package

# 运行应用
mvn spring-boot:run

# 使用特定配置文件运行（如果可用）
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 架构概览

### 包结构

- `com.example.test` - 主应用包
  - `ai/` - AI/LLM 集成，使用 LangChain4j
    - `service/` - AI 服务实现
    - `assitant/` - AI 助手接口和实现
    - `ollama/` - Ollama 集成示例
    - `chroma/` - 向量数据库集成（ChromaDB）
    - `file/` - 文档处理和解析工具
  - `paymentservice/` - 支付处理系统
    - `service/` - 支付服务实现
    - `controller/` - REST 控制器
    - `feign/` - 外部服务客户端
    - `config/` - 支付相关配置
  - `config/` - Spring 配置类
  - `demo/` - Java 特性演示（虚拟线程、NIO等）
  - `entity/` - JPA 实体
  - `mapper/` - MyBatis 映射器
  - `util/` - 工具类

### 核心组件

1. **AI集成**: 项目广泛使用 LangChain4j 进行各种AI任务，包括：
   - 文本翻译和生成
   - 文档处理和 RAG（检索增强生成）
   - 使用 ChromaDB 的向量嵌入
   - 多模型支持（OpenAI、Ollama）

2. **支付系统**: 实现完整的支付处理流程，包括：
   - 支付宝集成
   - 使用 RabbitMQ 的重试机制
   - 使用 Sentinel 的熔断器模式
   - 降级实现

3. **文档处理**: 全面的文档解析能力，支持：
   - PDF 文件
   - Word 文档（.doc、.docx）
   - 各种文本格式
   - 批量处理工作流

4. **现代Java特性**: 演示内容包括：
   - 用于并发编程的虚拟线程
   - 模式匹配和记录类型
   - 增强的 switch 表达式
   - NIO 和非阻塞 I/O

## 配置说明

- 使用 Undertow 而不是 Tomcat 以获得更好的性能
- Redis 和 Redisson 配置用于分布式锁
- 数据库连接使用 MariaDB 配合自定义 DataSource 配置
- 集成了多个消息队列系统（RocketMQ + RabbitMQ）

## 开发工作流

1. 主应用类是 `TestApplication.java`
2. 配置类位于 `config/` 包中
3. REST 端点在各自的控制器类中定义
4. 业务逻辑分离到服务类中
5. 数据库操作同时使用 JPA 和 MyBatis Plus

## 测试

项目包含测试工具，使用 TestContainers 进行集成测试，特别适用于使用 Ollama 容器测试 AI 组件。