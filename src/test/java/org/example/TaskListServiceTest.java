package org.example;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class TaskListServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskListServiceTest.class);

    @Container
    private GenericContainer postgres = new GenericContainer(DockerImageName.parse("postgres:12.12"))
            // provide Environment variables required by your container
            .withEnv("POSTGRES_USER", "admin")
            .withEnv("POSTGRES_PASSWORD", "admin")
            .withEnv("POSTGRES_DB", "tasklist")
            // provide an init scrip for the Postgres database
            .withClasspathResourceMapping("postgresInit.sql", "/docker-entrypoint-initdb.d/Init.sql", BindMode.READ_ONLY)
            .withExposedPorts(5432)
            // condition to be met for testcontainers to consider the postgres container ready
            .waitingFor(new WaitAllStrategy() // for container liveness checks
                // wait until the log message is printed
                .withStrategy(Wait.forLogMessage(".*database system is ready to accept connections.*", 1))
                // wait until port in pingable
                .withStrategy(Wait.defaultWaitStrategy())
            );

    @Container
    private GenericContainer nginx = new GenericContainer(DockerImageName.parse("nginx:1.23"))
            // start postgres before starting this container
            .dependsOn(postgres);

    private TaskListService taskListService;

    @BeforeEach
    void followContainerLogs() {
        postgres.followOutput(new Slf4jLogConsumer(LoggerFactory.getLogger("Postgres logger")));
        nginx.followOutput(new Slf4jLogConsumer(LoggerFactory.getLogger("Nginx logger")));
    }

    @BeforeEach
    void setUp() {
        final HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:" + postgres.getMappedPort(5432) + "/tasklist");
        dataSource.setUsername("admin");
        dataSource.setPassword("admin");
        taskListService = new TaskListService(dataSource);
    }

    @Test
    void shouldInsertNewEnteries() {
        final TaskListEntry entry = taskListService.insert(new TaskListEntry("write anything"));
        assertNotNull(entry);
        assertNotNull(entry.getId());
    }
}