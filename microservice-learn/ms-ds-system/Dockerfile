# ms-ds-system 构建：连接 MySQL
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
# 复制聚合工程根 pom
COPY pom.xml .
# 复制全部模块源码(根 pom <modules> 依赖这些目录存在)
COPY common common
COPY ms-ds-system ms-ds-system
COPY gateway gateway
COPY service-user service-user
COPY service-order service-order
COPY service-product service-product
# 单独构建 ms-ds-system 及其依赖(common), 忽略其他未完善模块
RUN mvn -q -pl ms-ds-system -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/ms-ds-system/target/*.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
