# syntax=docker/dockerfile:1
# NEW in Session 14: the notification service as a Docker image.   docker build -t notification-service:1.0.0 .
# Two stages: a big image with Maven and the JDK builds the jar, a small image with only the JRE runs it.

# ---- 1. build ----
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /src
# The pom first, alone: Docker caches this layer, so dependencies download again only when the pom changes
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
# Tests need Docker (Testcontainers) and already ran in CI, so we skip them here
RUN mvn -B -q package -DskipTests \
 && java -Djarmode=tools -jar target/notification-service-1.0.0.jar extract --layers --launcher --destination target/extracted

# ---- 2. run ----
FROM eclipse-temurin:25-jre-alpine
# Never run as root inside the container
RUN addgroup -S app && adduser -S app -G app
USER app
WORKDIR /app
# One layer per kind of file, from "changes rarely" to "changes on every commit".
# A new version of our code only replaces the last, small layer.
COPY --from=build /src/target/extracted/dependencies/ ./
COPY --from=build /src/target/extracted/spring-boot-loader/ ./
COPY --from=build /src/target/extracted/snapshot-dependencies/ ./
COPY --from=build /src/target/extracted/application/ ./
EXPOSE 8081
# Use up to 75% of the container's memory limit for the heap
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=5 \
  CMD wget -qO- http://localhost:8081/actuator/health/readiness || exit 1
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
