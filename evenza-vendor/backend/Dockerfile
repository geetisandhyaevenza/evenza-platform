# ============================================================
# EVENZA VENDOR — Dockerfile
# ============================================================
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copy Gradle wrapper and config
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Download dependencies
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon || true

# Copy source
COPY src src

# Build the application
RUN ./gradlew bootJar --no-daemon -x test

# ============================================================
# Runtime Stage
# ============================================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -S evenza && adduser -S evenza -G evenza

# Copy JAR
COPY --from=build /app/build/libs/evenza-vendor.jar app.jar

# Create uploads directory
RUN mkdir -p /app/uploads && chown -R evenza:evenza /app

USER evenza

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "/app/app.jar"]
