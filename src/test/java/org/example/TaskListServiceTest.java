package org.example;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class TaskListServiceTest {

    @Container
    private GenericContainer postgres = new GenericContainer(DockerImageName.parse("postgres:12.12"))
            .withEnv("POSTGRES_USER", "admin")
            .withEnv("POSTGRES_PASSWORD", "admin")
            .withEnv("POSTGRES_DB", "tasklist")
            .withClasspathResourceMapping("postgresInit.sql", "/docker-entrypoint-initdb.d/Init.sql", BindMode.READ_ONLY)
            .withExposedPorts(5432);

    private TaskListService taskListService;

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