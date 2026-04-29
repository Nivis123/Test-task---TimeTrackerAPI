package ru.prod.tracker.integration;

import org.springframework.test.context.TestPropertySource;
import ru.prod.tracker.mapper.TaskMapper;
import ru.prod.tracker.model.Task;
import ru.prod.tracker.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "jwt.expiration-ms=86400000",
        "jwt.secret=c2VjdXJlLXNlY3JldC1rZXktZm9yLWp3dC10YXNrLXRpbWUtdHJhY2tlci1hcGk"
})
class TaskMapperIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.sql.init.mode", () -> "always");
        registry.add("jwt.secret", () ->
                "c2VjdXJlLXNlY3JldC1rZXktZm9yLWp3dC10YXNrLXRpbWUtdHJhY2tlci1hcGk=");
        registry.add("jwt.expiration-ms", () -> 86400000);
    }

    @Autowired
    private TaskMapper taskMapper;

    @Test
    void insertAndFindById() {
        Task task = new Task();
        task.setName("Integration task");
        task.setDescription("desc");
        task.setStatus(TaskStatus.NEW);
        int rows = taskMapper.insert(task);
        assertThat(rows).isEqualTo(1);
        assertThat(task.getId()).isNotNull();

        Task found = taskMapper.findById(task.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Integration task");
        assertThat(found.getStatus()).isEqualTo(TaskStatus.NEW);
    }

    @Test
    void updateStatusShouldPersist() {
        Task task = new Task();
        task.setName("Task to update");
        task.setStatus(TaskStatus.NEW);
        taskMapper.insert(task);

        taskMapper.updateStatus(task.getId(), TaskStatus.DONE);
        Task updated = taskMapper.findById(task.getId());
        assertThat(updated.getStatus()).isEqualTo(TaskStatus.DONE);
    }
}