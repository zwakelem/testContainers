package org.example;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/*
    test containers using docker compose
 */
@Testcontainers
class TaskListServiceTest3 {

    @Container
    private TaskListPostgresContainer3 postgres = new TaskListPostgresContainer3();

    private TaskListService taskListService;

    @BeforeEach
    void followContainerLogs() {
        final String postgresUrl = String.format("jdbc:postgresql://%s:%d/tasklist",
                postgres.getHost(), postgres.getFirstMappedPort());

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