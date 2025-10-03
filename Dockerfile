FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
# 关键：-f 指向聚合 POM；-pl 用 :artifactId；-am 自动构建依赖模块
RUN mvn -f foodie-parent/pom.xml clean package -DskipTests -pl :foodie-server -am

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /build/foodie-server/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
