FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn -f foodie-parent/pom.xml -DskipTests clean package spring-boot:repackage -pl foodie-server -am

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /build/foodie-server/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]