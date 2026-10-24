# tiwoli
today in world literature - data, apps, scripts et al.

## Build

Requires Java 21 and Maven (or Docker):

    mvn package

## Run

The app is an executable JAR with embedded Tomcat, listening on port 8080:

    docker build -t tiwolij .
    docker run -p 8080:8080 \
      -e SPRING_DATASOURCE_URL=jdbc:mysql://<host>:3306/tiwolij \
      -e SPRING_DATASOURCE_USERNAME=tiwolij \
      -e SPRING_DATASOURCE_PASSWORD=<secret> \
      tiwolij

Defaults live in `src/main/resources/application.properties` (schema is validated, not
changed: `spring.jpa.hibernate.ddl-auto=validate`; the schema is in `schema.sql`). Any
property can be overridden by an environment variable. The `application.properties` in
the repository root is picked up when running locally from the project directory.
