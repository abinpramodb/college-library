# -------------------------------------------------------------
# Dockerfile for College Library Management System
# Optimized for Render Free Tier (Eclipse Temurin JDK/JRE 21)
# -------------------------------------------------------------

# Stage 1: Build Java Application
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY java-lms/src ./src
COPY java-lms/resources ./resources

RUN mkdir -p bin && \
    cp -r resources bin/ 2>/dev/null || true && \
    javac -d bin -sourcepath src $(find src -name "*.java") && \
    jar cfe LibraryManagementSystem.jar com.library.Main -C bin .

# Stage 2: Minimal Production JRE Runtime
FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app

# Copy standalone JAR
COPY --from=builder /app/LibraryManagementSystem.jar ./

# Copy web resources for static file serving
COPY java-lms/resources ./java-lms/resources
COPY java-lms/resources ./resources

# Render automatically injects the PORT environment variable
ENV PORT=10000
EXPOSE 10000

# Start headless server
ENTRYPOINT ["java", "-jar", "LibraryManagementSystem.jar"]
