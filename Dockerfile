# --- Stage 1: Build ---
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# Copy the pom.xml and download dependencies first (caching optimization)
COPY pom.xml .
#RUN mvn dependency:go-offline

# Copy the source code and build the jar
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Final ---
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

# Copy the generated jar from the build stage to the final stage
# Note: Ensure the source path matches the output of your build
COPY --from=build /app/target/muse-backend.jar app.jar

EXPOSE 2011

ENTRYPOINT ["java", "-jar", "app.jar"]

# docker build -t muse-backend .
# docker tag muse-backend jacque56/muse-backend:latest
#docker push jacque56/muse-backend:latest