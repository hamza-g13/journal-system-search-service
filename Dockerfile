# Steg 1: Bygg applikationen
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Bygg med fast-jar konfiguration (standard)
RUN mvn clean package -DskipTests -Dquarkus.package.type=fast-jar

# Steg 2: Kör applikationen
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Kopiera quarkus-app mappen som innehåller beroenden och körbar jar
COPY --from=build /app/target/quarkus-app/lib/ /app/lib/
COPY --from=build /app/target/quarkus-app/*.jar /app/
COPY --from=build /app/target/quarkus-app/app/ /app/app/
COPY --from=build /app/target/quarkus-app/quarkus/ /app/quarkus/

EXPOSE 8084
# Starta med quarkus-run.jar
CMD ["java", "-jar", "quarkus-run.jar"]
