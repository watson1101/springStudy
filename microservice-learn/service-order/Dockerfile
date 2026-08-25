# service-order 构建：连接 MySQL
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY common common
COPY service-order service-order
RUN mvn -q -pl service-order -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/service-order/target/*.jar app.jar
EXPOSE 8002
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
