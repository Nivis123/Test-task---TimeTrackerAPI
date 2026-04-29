package ru.prod.tracker.controller;

import ru.prod.tracker.config.JwtTokenProvider;
import ru.prod.tracker.dto.TaskCreateRequest;
import ru.prod.tracker.dto.TaskStatusUpdateRequest;
import ru.prod.tracker.mapper.TaskMapper;
import ru.prod.tracker.mapper.TimeRecordMapper;
import ru.prod.tracker.mapper.UserMapper;
import ru.prod.tracker.model.TaskStatus;
import ru.prod.tracker.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import({ru.prod.tracker.config.SecurityConfig.class, ru.prod.tracker.config.JwtAuthenticationFilter.class, ru.prod.tracker.config.JwtTokenProvider.class})
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private JwtTokenProvider tokenProvider;

    @MockitoBean
    private TaskMapper taskMapper;

    @MockitoBean
    private TimeRecordMapper timeRecordMapper;

    @MockitoBean
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void createTaskShouldReturn201() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest();
        request.setName("New task");
        request.setDescription("desc");

        when(taskService.createTask(any())).thenReturn(new ru.prod.tracker.dto.TaskResponse() {{
            setId(1L);
            setName("New task");
            setDescription("desc");
            setStatus(TaskStatus.NEW);
        }});

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void getTaskShouldReturnOk() throws Exception {
        when(taskService.getTask(1L)).thenReturn(new ru.prod.tracker.dto.TaskResponse() {{
            setId(1L);
            setName("Task");
            setStatus(TaskStatus.NEW);
        }});

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Task"));
    }

    @Test
    @WithMockUser
    void updateStatusShouldReturnOk() throws Exception {
        TaskStatusUpdateRequest update = new TaskStatusUpdateRequest();
        update.setStatus(TaskStatus.DONE);

        when(taskService.updateStatus(any(), any())).thenReturn(new ru.prod.tracker.dto.TaskResponse() {{
            setId(1L);
            setStatus(TaskStatus.DONE);
        }});

        mockMvc.perform(put("/api/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }
}