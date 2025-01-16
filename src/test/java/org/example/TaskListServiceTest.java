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
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class TaskListServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskListServiceTest.class);

    @Container
    private GenericContainer postgres = new TaskListPostgresContainer();

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