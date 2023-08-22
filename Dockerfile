# Setting Base Images
FROM dvmarques/openjdk-17-jdk-alpine-with-timezone
EXPOSE 8080
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]