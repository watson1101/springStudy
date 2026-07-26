FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/order-1.0.0-SNAPSHOT.jar order.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "order.jar"]