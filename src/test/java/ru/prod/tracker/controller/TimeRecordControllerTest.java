package ru.prod.tracker.controller;

import org.springframework.test.context.TestPropertySource;
import ru.prod.tracker.config.JwtAuthenticationFilter;
import ru.prod.tracker.config.JwtTokenProvider;
import ru.prod.tracker.config.SecurityConfig;
import ru.prod.tracker.dto.TaskCreateRequest;
import ru.prod.tracker.dto.TaskStatusUpdateRequest;
import ru.prod.tracker.dto.TimeRecordCreateRequest;
import ru.prod.tracker.dto.TimeRecordResponse;
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
import ru.prod.tracker.service.TimeRecordService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TimeRecordController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtTokenProvider.class})
class TimeRecordControllerTest {

    @MockitoBean
    private TaskMapper taskMapper;

    @MockitoBean
    private TimeRecordMapper timeRecordMapper;

    @MockitoBean
    private UserMapper userMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TimeRecordService timeRecordService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void createTimeRecordShouldReturn201() throws Exception {
        TimeRecordCreateRequest req = new TimeRecordCreateRequest();
        req.setEmployeeId(1L);
        req.setStartTime(LocalDateTime.of(2024,1,1,9,0));
        req.setEndTime(LocalDateTime.of(2024,1,1,12,0));
        req.setDescriptionWork("work");

        TimeRecordResponse resp = new TimeRecordResponse();
        resp.setId(10L);
        resp.setEmployeeId(1L);
        resp.setTaskId(5L);
        when(timeRecordService.createTimeRecord(eq(5L), any())).thenReturn(resp);

        mockMvc.perform(post("/api/tasks/5/timerecords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @WithMockUser
    void getTimeRecordsShouldReturnOk() throws Exception {
        TimeRecordResponse rec = new TimeRecordResponse();
        rec.setId(1L);
        rec.setEmployeeId(1L);
        when(timeRecordService.getTimeRecords(eq(1L), any(), any())).thenReturn(List.of(rec));

        mockMvc.perform(get("/api/timerecords")
                        .param("employeeId", "1")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}