# ---- 构建阶段 ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /build

# 复制整个项目目录（包含所有pom和模块）
COPY . .

# 编译所有依赖（包括 SNAPSHOT）
RUN mvn clean package -DskipTests -pl foodie-server -am
# -pl foodie-server：只打包 foodie-server（主服务）
# -am：会自动构建其依赖的 parent、common、pojo 等子模块

# ---- 运行阶段 ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# 只复制主服务 jar
COPY --from=build /build/foodie-server/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]