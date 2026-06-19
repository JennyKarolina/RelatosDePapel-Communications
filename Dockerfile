# Build del proyecto (Multi-Stage)
# --------------------------------
FROM maven:3.9.12-eclipse-temurin-25 AS build
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:25-jre
EXPOSE 8082
COPY --from=build /target/communications-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
