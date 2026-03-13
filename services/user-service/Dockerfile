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
RUN mvn -q -B -pl services/user-service -am dependency:go-offline

# copy source code
COPY libs/common-security/src libs/common-security/src
COPY services/user-service/src services/user-service/src

# build user-service with dependencies
RUN mvn -q -DskipTests -pl services/user-service -am package

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/services/user-service/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]