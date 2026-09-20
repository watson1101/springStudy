# service-hotnews-consumer 构建：热点资讯消费服务
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY common common
COPY service-hotnews-consumer service-hotnews-consumer
# 仅保留本次构建所需模块，避免父 pom 引用缺失子模块导致构建失败
RUN awk 'BEGIN{s=0} /<modules>/{s=1; print "    <modules>"; print "        <module>common</module>"; print "        <module>service-hotnews-consumer</module>"; next} /<\/modules>/{s=0; print "    </modules>"; next} s==1{next} {print}' pom.xml > pom-trim.xml && mv pom-trim.xml pom.xml
RUN mvn -q -pl service-hotnews-consumer -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/service-hotnews-consumer/target/*.jar /app/app.jar
RUN (id -u 1000 >/dev/null 2>&1 || useradd -u 1000 -r -s /bin/false appuser) \
 && chown -R 1000:1000 /app
USER 1000

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

EXPOSE 8009
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app/app.jar" ]
