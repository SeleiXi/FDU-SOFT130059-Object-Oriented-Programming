FROM maven:3.8.4-openjdk-11-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/chess-game-1.0-SNAPSHOT.jar /app/chess-game.jar
ENTRYPOINT ["java", "-jar", "/app/chess-game.jar"] 