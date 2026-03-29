# STAGE 1 - BUILD
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn -B -q dependency:resolve dependency:resolve-plugins

COPY src ./src
RUN mvn -B -q clean package -DskipTests


# STAGE 2 - RUNTIME
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN addgroup --system spring && adduser --system spring --ingroup spring

COPY --from=builder /build/target/CustomerService.jar app.jar

EXPOSE 8080

USER spring

ENTRYPOINT ["java", "-jar", "app.jar"]