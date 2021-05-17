# lightweight java image
FROM openjdk:17-slim

EXPOSE 1502

WORKDIR /app

COPY target/Server-jar-with-dependencies.jar /app

ENTRYPOINT ["java", "-jar", "/app/Server-jar-with-dependencies.jar"]
