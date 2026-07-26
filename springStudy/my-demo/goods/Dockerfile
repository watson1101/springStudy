FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/goods-1.0.0-SNAPSHOT.jar goods.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "goods.jar"]