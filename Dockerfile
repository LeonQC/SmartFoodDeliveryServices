# ---- 构建阶段 ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /build

# 复制父pom和所有子module
COPY foodie-parent /build/foodie-parent
COPY foodie-common /build/foodie-common
COPY foodie-pojo /build/foodie-pojo
COPY foodie-server /build/foodie-server

WORKDIR /build/foodie-server

# 构建 jar（包括所有依赖）
RUN mvn clean package -DskipTests

# ---- 运行阶段 ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /build/foodie-server/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]