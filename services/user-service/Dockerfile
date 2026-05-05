# syntax=docker/dockerfile:1.7

FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

# Maven reactor needs every module pom.xml referenced by the root aggregator.
COPY libs/common-security/pom.xml libs/common-security/pom.xml
COPY libs/common-events/pom.xml libs/common-events/pom.xml
COPY libs/common-exceptions/pom.xml libs/common-exceptions/pom.xml

COPY services/auth-service/pom.xml services/auth-service/pom.xml
COPY services/user-service/pom.xml services/user-service/pom.xml
COPY services/social-graph-service/pom.xml services/social-graph-service/pom.xml
COPY services/notification-service/pom.xml services/notification-service/pom.xml
COPY services/post-service/pom.xml services/post-service/pom.xml
COPY services/feed-service/pom.xml services/feed-service/pom.xml
COPY services/reaction-service/pom.xml services/reaction-service/pom.xml
COPY services/comment-service/pom.xml services/comment-service/pom.xml
COPY services/media-service/pom.xml services/media-service/pom.xml

RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -B -T 1C -pl services/user-service -am dependency:go-offline

COPY libs/common-security/src libs/common-security/src
COPY libs/common-exceptions/src libs/common-exceptions/src
COPY services/user-service/src services/user-service/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -B -T 1C -DskipTests -pl services/user-service -am package

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/services/user-service/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
