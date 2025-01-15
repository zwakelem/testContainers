package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
class ContainerTest {

    @Container
    private GenericContainer tomcat = new GenericContainer<>(DockerImageName.parse("tomcat"));

    /*
        static containers start once for the duration of the class execution
     */
    @Container
    private static GenericContainer redis = new GenericContainer(DockerImageName.parse("redis"));

    @Test
    void firstContainerTest() {
        /*final String postgresLogs = tomcat.getLogs();
        final String redisLogs = redis.getLogs();*/

        Assertions.assertTrue(tomcat.isRunning());
        Assertions.assertTrue(redis.isRunning());
        System.out.println("tomcat name " + tomcat.getContainerName());
        System.out.println("redis name " + redis.getContainerName());


    }

    @Test
    void secondContainerTest() {
        Assertions.assertTrue(tomcat.isRunning());
        Assertions.assertTrue(redis.isRunning());
        System.out.println("tomcat name " + tomcat.getContainerName());
        System.out.println("redis name " + redis.getContainerName());
    }

}
