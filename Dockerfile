# Stage 1: Build the application
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy the Gradle wrapper and configuration files
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Give execution permission to the Gradle wrapper
RUN chmod +x gradlew

# Copy the application source code
COPY src src

# Build the application, skipping tests.
# Cache mount (BuildKit) giữ Gradle distribution + dependencies trong /root/.gradle giữa các lần build
# → không phải tải lại gradle-x.x-bin.zip (~8 phút trên VPS) mỗi khi src thay đổi.
RUN --mount=type=cache,target=/root/.gradle ./gradlew bootJar --no-daemon -x test

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# curl dùng cho HEALTHCHECK trong docker-compose.yml (gọi /actuator/health)
RUN apk add --no-cache curl

# Copy the built JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
