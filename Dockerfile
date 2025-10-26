# Multi-stage build for Railway
FROM openjdk:11-jdk-slim AS build

# Set working directory
WORKDIR /app

# Install Maven directly instead of using wrapper
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src src

# Build the application with Lombok annotation processing
RUN mvn clean package -DskipTests -B

# Production stage
FROM openjdk:11-jre-slim

# Set working directory
WORKDIR /app

# Copy the JAR file from build stage
COPY --from=build /app/target/*.jar app.jar

# Create uploads directory
RUN mkdir -p /tmp/ats-uploads

# Expose port
EXPOSE 8080

# Add environment variables for Railway
ENV SPRING_PROFILES_ACTIVE=prod
ENV CONTEXT_PATH=""
ENV UPLOAD_PATH=/tmp/ats-uploads

# Run the application
CMD ["java", "-jar", "app.jar"]