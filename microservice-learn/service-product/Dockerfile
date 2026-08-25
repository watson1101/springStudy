# service-product 构建：连接 PostgreSQL + Sentinel
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY common common
COPY service-product service-product
RUN mvn -q -pl service-product -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/service-product/target/*.jar app.jar
EXPOSE 8003
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
