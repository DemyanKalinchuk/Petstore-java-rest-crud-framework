FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./
COPY checkstyle ./checkstyle
RUN mvn -ntp -q -e -DskipTests dependency:go-offline

FROM maven:3.9.9-eclipse-temurin-21
WORKDIR /app
COPY --from=build /root/.m2 /root/.m2
COPY . /app
ENV ENV_PROFILE=dev
CMD ["bash", "-lc", "mvn -ntp -P${ENV_PROFILE} -Denv=${ENV_PROFILE} verify"]
