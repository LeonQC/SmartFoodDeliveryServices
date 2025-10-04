# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .

# 1) 先构建并安装所有依赖到本地仓库（不会对普通库模块做 repackage）
RUN mvn -f foodie-parent/pom.xml -DskipTests clean install -am

# 2) 仅对可执行模块 repackage（不会波及 foodie-common/foodie-pojo）
RUN mvn -f foodie-parent/pom.xml -DskipTests -pl :foodie-server -am spring-boot:repackage

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /build/foodie-server/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
