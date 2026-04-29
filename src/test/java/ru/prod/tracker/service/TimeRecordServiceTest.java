package ru.prod.tracker.service;

import ru.prod.tracker.dto.TimeRecordCreateRequest;
import ru.prod.tracker.dto.TimeRecordResponse;
import ru.prod.tracker.exception.ResourceNotFoundException;
import ru.prod.tracker.mapper.TaskMapper;
import ru.prod.tracker.mapper.TimeRecordMapper;
import ru.prod.tracker.model.Task;
import ru.prod.tracker.model.TimeRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeRecordServiceTest {

    @Mock
    private TimeRecordMapper timeRecordMapper;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TimeRecordService timeRecordService;

    @Test
    void createTimeRecordShouldSucceed() {
        when(taskMapper.findById(1L)).thenReturn(new Task());
        TimeRecordCreateRequest req = new TimeRecordCreateRequest();
        req.setEmployeeId(100L);
        req.setStartTime(LocalDateTime.of(2024,5,1,9,0));
        req.setEndTime(LocalDateTime.of(2024,5,1,12,0));
        req.setDescriptionWork("work");

        ArgumentCaptor<TimeRecord> captor = ArgumentCaptor.forClass(TimeRecord.class);
        doAnswer(inv -> {
            TimeRecord tr = inv.getArgument(0);
            tr.setId(10L);
            return 1;
        }).when(timeRecordMapper).insert(captor.capture());

        TimeRecordResponse resp = timeRecordService.createTimeRecord(1L, req);
        assertThat(resp.getId()).isEqualTo(10L);
        assertThat(resp.getTaskId()).isEqualTo(1L);
        TimeRecord saved = captor.getValue();
        assertThat(saved.getEmployeeId()).isEqualTo(100L);
    }

    @Test
    void createTimeRecordShouldThrowWhenTaskNotFound() {
        when(taskMapper.findById(99L)).thenReturn(null);
        TimeRecordCreateRequest req = new TimeRecordCreateRequest();
        req.setEmployeeId(1L);
        req.setStartTime(LocalDateTime.now());
        req.setEndTime(LocalDateTime.now().plusHours(1));
        assertThatThrownBy(() -> timeRecordService.createTimeRecord(99L, req))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getTimeRecordsShouldReturnList() {
        LocalDate start = LocalDate.of(2024,5,1);
        LocalDate end = LocalDate.of(2024,5,31);
        TimeRecord record = new TimeRecord();
        record.setId(1L);
        record.setEmployeeId(100L);
        when(timeRecordMapper.findByEmployeeIdAndPeriod(eq(100L), any(), any()))
                .thenReturn(List.of(record));

        List<TimeRecordResponse> responses = timeRecordService.getTimeRecords(100L, start, end);
        assertThat(responses).hasSize(1);
    }
}
