# service-transaction 构建：交易/支付模拟服务（2s延时+Feign调积分）
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY common common
COPY service-transaction service-transaction
RUN mvn -q -pl service-transaction -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/service-transaction/target/*.jar /app/app.jar
RUN useradd -u 1000 -r -s /bin/false appuser \
 && chown -R 1000:1000 /app
USER 1000

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

EXPOSE 8004
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app/app.jar" ]
