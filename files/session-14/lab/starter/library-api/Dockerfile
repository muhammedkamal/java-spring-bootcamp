# syntax=docker/dockerfile:1
# TODO 5: the Library API as a Docker image.   docker build -t library-api:1.0.0 .
# Use ../../../system/task-api/Dockerfile as your model, and write each line yourself.
#
# Stage 1 "build" FROM maven:3.9-eclipse-temurin-25
#   - copy pom.xml first and run "mvn -B -q dependency:go-offline" (this layer is cached until the pom changes)
#   - copy src/ and run "mvn -B -q package -DskipTests" (CI already ran the tests)
#   - extract the jar into layers: java -Djarmode=tools -jar target/library-api-1.0.0.jar extract --layers --launcher --destination target/extracted
#
# Stage 2 FROM eclipse-temurin:25-jre-alpine
#   - create a user "app" and switch to it (never run as root)
#   - copy the four layers, least often changed first: dependencies, spring-boot-loader, snapshot-dependencies, application
#   - EXPOSE 8080, JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=75
#   - HEALTHCHECK with wget on /actuator/health/readiness
#   - ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
