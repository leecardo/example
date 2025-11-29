# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.4.2 application running on Java 21 that demonstrates various Java features, AI integration with LangChain4j, payment processing systems, and middleware implementations. The project uses Undertow as the web server instead of Tomcat.

## Key Technologies and Dependencies

- **Spring Boot 3.4.2** with Java 21
- **LangChain4j 1.0.0-rc1** for AI/LLM integration (OpenAI, Ollama)
- **Database**: MariaDB with JPA and MyBatis Plus
- **Caching**: Redis with Lettuce client and Redisson
- **Message Queue**: RocketMQ and RabbitMQ
- **File Processing**: Apache Tika, PDFBox, Apache POI for document parsing
- **HTTP Client**: OkHttp and Retrofit
- **ML Libraries**: ND4J for numerical computing
- **Testing**: JUnit Jupiter and TestContainers

## Build and Development Commands

```bash
# Build the project
mvn clean compile

# Run tests
mvn test

# Package the application
mvn clean package

# Run the application
mvn spring-boot:run

# Run with specific profile (if available)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Architecture Overview

### Package Structure

- `com.example.test` - Main application package
  - `ai/` - AI/LLM integration with LangChain4j
    - `service/` - AI service implementations
    - `assitant/` - AI assistant interfaces and implementations
    - `ollama/` - Ollama integration examples
    - `chroma/` - Vector database integration (ChromaDB)
    - `file/` - Document processing and parsing utilities
  - `paymentservice/` - Payment processing system
    - `service/` - Payment service implementations
    - `controller/` - REST controllers
    - `feign/` - External service clients
    - `config/` - Payment-related configurations
  - `config/` - Spring configuration classes
  - `demo/` - Java feature demonstrations (virtual threads, NIO, etc.)
  - `entity/` - JPA entities
  - `mapper/` - MyBatis mappers
  - `util/` - Utility classes

### Key Components

1. **AI Integration**: The project extensively uses LangChain4j for various AI tasks including:
   - Text translation and generation
   - Document processing and RAG (Retrieval-Augmented Generation)
   - Vector embeddings with ChromaDB
   - Multiple model support (OpenAI, Ollama)

2. **Payment System**: Implements a complete payment processing flow with:
   - Alipay integration
   - Retry mechanisms with RabbitMQ
   - Circuit breaker patterns with Sentinel
   - Fallback implementations

3. **Document Processing**: Comprehensive document parsing capabilities supporting:
   - PDF files
   - Word documents (.doc, .docx)
   - Various text formats
   - Batch processing workflows

4. **Modern Java Features**: Demonstrations of:
   - Virtual threads for concurrent programming
   - Pattern matching and records
   - Enhanced switch expressions
   - NIO and non-blocking I/O

## Configuration Notes

- Uses Undertow instead of Tomcat for better performance
- Redis and Redisson are configured for distributed locking
- Database connection uses MariaDB with custom DataSource configuration
- Multiple message queue systems are integrated (RocketMQ + RabbitMQ)

## Development Workflow

1. The main application class is `TestApplication.java`
2. Configuration classes are in the `config/` package
3. REST endpoints are defined in respective controller classes
4. Business logic is separated into service classes
5. Database operations use both JPA and MyBatis Plus

## Testing

The project includes test utilities with TestContainers for integration testing, particularly for testing AI components with Ollama containers.