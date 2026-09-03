# service-product 构建：商品服务（Sentinel + MySQL）
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY common common
COPY service-product service-product
RUN mvn -q -pl service-product -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/service-product/target/*.jar /app/app.jar
RUN useradd -u 1000 -r -s /bin/false appuser \
 && chown -R 1000:1000 /app
USER 1000

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

EXPOSE 8003
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app/app.jar" ]
