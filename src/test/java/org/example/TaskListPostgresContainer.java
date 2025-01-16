package org.example;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;

public class TaskListPostgresContainer extends GenericContainer {

    public TaskListPostgresContainer() {
        super(new ImageFromDockerfile("tasklist-postgres-container")
            .withFileFromClasspath("Dockerfile", "postgres/Dockerfile")
            .withFileFromClasspath("postgresInit.sql", "postgres/postgresInit.sql"));
        this.withEnv("POSTGRES_USER", "admin")
            .withEnv("POSTGRES_PASSWORD", "admin")
            .withEnv("POSTGRES_DB", "tasklist")
            // provide an init scrip for the Postgres database
            .withClasspathResourceMapping("postgres/postgresInit.sql",
                    "/docker-entrypoint-initdb.d/Init.sql",
                    BindMode.READ_ONLY)
            .withExposedPorts(5432)
            // condition to be met for testcontainers to consider the postgres container ready
            .waitingFor(new WaitAllStrategy() // for container liveness checks
                // wait until the log message is printed
                .withStrategy(Wait.forLogMessage(".*database system is ready to accept connections.*", 1))
                // wait until port in pingable
                .withStrategy(Wait.defaultWaitStrategy())
            );
    }
}
