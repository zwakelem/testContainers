package org.example;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/*
    test containers using docker compose
 */
@Testcontainers
class TaskListServiceTest2 {

    @Container
    private DockerComposeContainer environment = new DockerComposeContainer(
        new File("src/test/resources/docker-compose.yml"))
            .withExposedService("postgres", 5432)
            .withExposedService("nginx", 80);

    private TaskListService taskListService;

    @BeforeEach
    void followContainerLogs() {
        final String postgresHost = environment.getServiceHost("postgres", 5432);
        final int postgresPort = environment.getServicePort("postgres", 5432);
        final String postgresUrl = String.format("jdbc:postgresql://%s:%d/tasklist", postgresHost, postgresPort);

        final HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(postgresUrl);
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