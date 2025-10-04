# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .

# 先打整个工程（不跑 repackage），需要依赖的 jar 都会产出到本地仓库
RUN mvn -f foodie-parent/pom.xml -DskipTests clean package -am

# 仅在可执行模块里做 repackage（不会波及到 foodie-common）
WORKDIR /build/foodie-server
RUN mvn -DskipTests spring-boot:repackage

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /build/foodie-server/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
