# 多阶段构建：任意微服务模块通用 Dockerfile
# 用法：docker build --build-arg MODULE=service-user -t ms/service-user .
# 或由 docker-compose.full.yml 调用（每个服务已指定 build 目录）

# ---- 构建阶段 ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
# 先拷贝父 POM + 公共模块，最大化利用缓存
COPY pom.xml .
COPY common common
COPY gateway gateway
COPY service-user service-user
COPY service-order service-order
COPY service-product service-product
ARG MODULE
RUN mvn -q -pl ${MODULE} -am clean package -DskipTests

# ---- 运行阶段 ----
FROM eclipse-temurin:17-jre
ARG MODULE
WORKDIR /app
COPY --from=build /build/${MODULE}/target/*.jar app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
