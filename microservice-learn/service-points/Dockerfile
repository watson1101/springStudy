# service-points 构建：积分服务（账户/发放/消耗/流水）
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY common common
COPY service-points service-points
RUN mvn -q -pl service-points -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/service-points/target/*.jar /app/app.jar
RUN useradd -u 1000 -r -s /bin/false appuser \
 && chown -R 1000:1000 /app
USER 1000

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

EXPOSE 8005
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app/app.jar" ]
