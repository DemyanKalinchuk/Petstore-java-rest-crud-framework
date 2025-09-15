FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -ntp -DskipTests dependency:go-offline
COPY src ./src
COPY checkstyle ./checkstyle
RUN mvn -q -ntp -DskipITs=false verify

FROM eclipse-temurin:21-jre
WORKDIR /app
CMD ["bash","-lc","echo 'No runtime app; use docker compose to run smokeTests.'"]
