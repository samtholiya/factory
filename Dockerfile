#
# Build stage
#
FROM maven:3.8.3-openjdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -Dmaven.test.skip


#
# Package stage
#
FROM openjdk:17-jdk-alpine3.13
COPY --from=build /home/app/target/machine-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]