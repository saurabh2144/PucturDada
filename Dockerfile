# Base image with Maven and JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build


# Set working directory inside the container
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .

RUN mvn dependency:go-offline

# Copy the entire project into the container
COPY . .

# Package the application (skip tests if needed)
RUN mvn clean package -DskipTests

# Use a smaller runtime image (JDK only) for final stage
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the JAR file from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
