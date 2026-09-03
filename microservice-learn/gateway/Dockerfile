# ms-gateway 构建：Spring Cloud Gateway (WebFlux + Nacos)
# 多阶段构建：build 阶段在服务器上 maven 打包；runtime 阶段以 eclipse-temurin JRE 非 root 运行
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
# 按需复制：父 pom + common 依赖 + gateway 模块
COPY pom.xml .
COPY common common
COPY gateway gateway
RUN mvn -q -pl gateway -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
# 提前复制 jar，并创建非 root 用户 (uid=1000)，与 K8s securityContext runAsUser=1000 对齐
COPY --from=build /build/gateway/target/*.jar /app/app.jar
RUN useradd -u 1000 -r -s /bin/false appuser \
 && chown -R 1000:1000 /app
USER 1000

# JVM 启动参数：MaxRAMPercentage 与 cgroup limits.memory 对齐；额外 JAVA_OPTS 可由部署环境覆盖
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

EXPOSE 8000
# 使用 exec 形式，确保 PID=1 是 java 进程，信号能正常传递
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app/app.jar" ]
