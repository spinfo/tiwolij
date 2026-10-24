FROM maven:3-eclipse-temurin-21 AS maven
WORKDIR /tmp/tiwolij
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src src
RUN mvn -B -q package -DskipTests

FROM eclipse-temurin:21-jre
RUN useradd --system --no-create-home tiwolij
COPY --from=maven /tmp/tiwolij/target/TiwoliJ-*.jar /srv/tiwolij.jar
USER tiwolij
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/srv/tiwolij.jar"]
