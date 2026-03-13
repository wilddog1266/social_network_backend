FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# root pom
COPY pom.xml .

# module poms
COPY libs/common-security/pom.xml libs/common-security/pom.xml
COPY services/auth-service/pom.xml services/auth-service/pom.xml
COPY services/user-service/pom.xml services/user-service/pom.xml
COPY services/social-graph-service/pom.xml services/social-graph-service/pom.xml

# download dependencies
RUN mvn -q -B -pl services/social-graph-service -am dependency:go-offline

# copy source code
COPY libs/common-security/src libs/common-security/src
COPY services/social-graph-service/src services/social-graph-service/src

# build social-graph-service with dependencies
RUN mvn -q -DskipTests -pl services/social-graph-service -am package

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/services/social-graph-service/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]