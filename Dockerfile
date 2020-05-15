FROM maven:3.5.2-jdk-8-alpine AS MAVEN_BUILD

COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn -T 1C clean install -Dmaven.test.skip -DskipTests

FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/air5-web.jar /app/
ENTRYPOINT ["java", "-jar", "air5-web.jar"]
