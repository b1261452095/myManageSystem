# 使用官方OpenJDK 17镜像作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制Maven包装器文件
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# 给Maven包装器执行权限
RUN chmod +x ./mvnw

# 下载依赖（利用Docker缓存层）
RUN ./mvnw dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用
RUN ./mvnw clean package -DskipTests

# 暴露端口
EXPOSE 8080

# 设置JVM参数
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# 运行应用
CMD ["java", "-jar", "target/admin-system-backend-1.0.0.jar"]
