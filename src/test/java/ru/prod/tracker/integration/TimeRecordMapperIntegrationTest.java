package ru.prod.tracker.integration;

import org.springframework.test.context.TestPropertySource;
import ru.prod.tracker.mapper.TaskMapper;
import ru.prod.tracker.mapper.TimeRecordMapper;
import ru.prod.tracker.model.Task;
import ru.prod.tracker.model.TaskStatus;
import ru.prod.tracker.model.TimeRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "jwt.expiration-ms=86400000",
        "jwt.secret=c2VjdXJlLXNlY3JldC1rZXktZm9yLWp3dC10YXNrLXRpbWUtdHJhY2tlci1hcGk"
})
class TimeRecordMapperIntegrationTest {

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
    }

    @Autowired
    private TimeRecordMapper timeRecordMapper;

    @Autowired
    private TaskMapper taskMapper;

    private Long taskId;

    @BeforeEach
    void setUp() {
        Task task = new Task();
        task.setName("Task for records");
        task.setStatus(TaskStatus.NEW);
        taskMapper.insert(task);
        taskId = task.getId();
    }

    @Test
    void insertAndFindByPeriod() {
        TimeRecord record = new TimeRecord();
        record.setEmployeeId(200L);
        record.setTaskId(taskId);
        LocalDateTime start = LocalDateTime.of(2024, 6, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(2024, 6, 1, 12, 0);
        record.setStartTime(start);
        record.setEndTime(end);
        record.setDescriptionWork("Development");
        timeRecordMapper.insert(record);

        List<TimeRecord> list = timeRecordMapper.findByEmployeeIdAndPeriod(200L,
                LocalDateTime.of(2024, 6, 1, 0, 0),
                LocalDateTime.of(2024, 6, 1, 23, 59));
        assertThat(list).hasSize(1);
        TimeRecord found = list.get(0);
        assertThat(found.getEmployeeId()).isEqualTo(200L);
        assertThat(found.getTaskId()).isEqualTo(taskId);
        assertThat(found.getDescriptionWork()).isEqualTo("Development");
    }
}