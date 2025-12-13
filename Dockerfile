FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8086

ENTRYPOINT ["java","-jar","app.jar"]
