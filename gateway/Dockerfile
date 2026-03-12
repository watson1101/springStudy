FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/gateway-1.0.0-SNAPSHOT.jar gateway.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "gateway.jar"]