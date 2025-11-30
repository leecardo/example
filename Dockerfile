# Java Spring Boot Application Dockerfile

# 使用多阶段构建
FROM maven:3.9-openjdk-21 AS builder

# 设置工作目录
WORKDIR /app

# 复制pom文件
COPY cloud-service/pom.xml .

# 下载依赖（利用Docker缓存）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY cloud-service/src ./src

# 构建应用
ARG MVN_PROFILE=prod
RUN mvn clean package -DskipTests -P${MVN_PROFILE}

# 运行时镜像
FROM openjdk:21-jre-slim

# 安装必要的工具
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 创建应用用户
RUN addgroup --system spring && \
    adduser --system spring --ingroup spring

# 设置工作目录
WORKDIR /app

# 复制构建的jar文件
COPY --from=builder /app/target/*.jar app.jar

# 更改文件所有者
RUN chown spring:spring app.jar

# 切换到应用用户
USER spring

# 设置JVM参数
ENV JAVA_OPTS="-XX:+UseG1GC \
              -XX:+UseContainerSupport \
              -XX:MaxRAMPercentage=75.0 \
              -Djava.security.egd=file:/dev/./urandom \
              -Dspring.profiles.active=docker"

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]