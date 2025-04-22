FROM eclipse-temurin:21-jre-jammy

# 设置工作目录
WORKDIR /app

# 将jar包复制到工作目录
COPY target/transaction-demo-1.0-SNAPSHOT.jar demo.jar

# 暴露应用程序运行的端口
EXPOSE 8080

# 运行应用程序，使用java命令启动jar包
ENTRYPOINT ["java", "-jar", "demo.jar"]