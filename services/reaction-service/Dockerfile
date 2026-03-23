FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# root pom
COPY pom.xml .

# module poms
COPY libs/common-security/pom.xml libs/common-security/pom.xml
COPY libs/common-events/pom.xml libs/common-events/pom.xml
COPY libs/common-exceptions/pom.xml libs/common-exceptions/pom.xml
COPY services/auth-service/pom.xml services/auth-service/pom.xml
COPY services/user-service/pom.xml services/user-service/pom.xml
COPY services/social-graph-service/pom.xml services/social-graph-service/pom.xml
COPY services/notification-service/pom.xml services/notification-service/pom.xml
COPY services/post-service/pom.xml services/post-service/pom.xml
COPY services/reaction-service/pom.xml services/reaction-service/pom.xml
COPY services/comment-service/pom.xml services/comment-service/pom.xml


# download dependencies
RUN mvn -q -B -pl services/reaction-service -am dependency:go-offline

# copy source code
COPY libs/common-security/src libs/common-security/src
COPY libs/common-exceptions/src libs/common-exceptions/src
COPY libs/common-events/src libs/common-events/src
COPY services/reaction-service/src services/reaction-service/src

# build reaction-service with dependencies
RUN mvn -q -DskipTests -pl services/reaction-service -am package

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/services/reaction-service/target/*.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]