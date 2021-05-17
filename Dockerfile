# lightweight java image
FROM openjdk:17-slim

# websocket handshake port
EXPOSE 1502

WORKDIR /app

# Only thing need for the server to run is the fat jar file
COPY target/Server-jar-with-dependencies.jar /app

ENTRYPOINT ["java", "-jar", "/app/Server-jar-with-dependencies.jar"]
