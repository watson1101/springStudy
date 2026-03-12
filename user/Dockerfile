FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/user-1.0.0-SNAPSHOT.jar user.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "user.jar"]