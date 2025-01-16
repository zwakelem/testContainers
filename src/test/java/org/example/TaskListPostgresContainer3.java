package org.example;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;

public class TaskListPostgresContainer3 extends PostgreSQLContainer {

    public TaskListPostgresContainer3() {
        super("postgres:14-alpine");
        this.withExposedPorts(5432);
        this.withUsername("admin")
            .withPassword("admin")
            .withDatabaseName("tasklist")
            .withClasspathResourceMapping("postgres/postgresInit.sql",
                "/docker-entrypoint-initdb.d/Init.sql",
                BindMode.READ_ONLY)
            .withExposedPorts(5432)
            .waitingFor(new WaitAllStrategy() // for container liveness checks
                    // wait until the log message is printed
                    .withStrategy(Wait.forLogMessage(".*database system is ready to accept connections.*", 1))
                    // wait until port in pingable
                    .withStrategy(Wait.defaultWaitStrategy())
            );
    }
}
