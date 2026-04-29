package ru.prod.tracker.service;

import ru.prod.tracker.dto.*;
import ru.prod.tracker.exception.ResourceNotFoundException;
import ru.prod.tracker.mapper.TaskMapper;
import ru.prod.tracker.mapper.TimeRecordMapper;
import ru.prod.tracker.model.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.prod.tracker.model.TimeRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeRecordService {

    private final TimeRecordMapper timeRecordMapper;
    private final TaskMapper taskMapper;

    public TimeRecordService(TimeRecordMapper timeRecordMapper, TaskMapper taskMapper) {
        this.timeRecordMapper = timeRecordMapper;
        this.taskMapper = taskMapper;
    }

    @Transactional
    public TimeRecordResponse createTimeRecord(Long taskId, TimeRecordCreateRequest request) {
        Task task = taskMapper.findById(taskId);
        if (task == null) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        TimeRecord record = new TimeRecord();
        record.setEmployeeId(request.getEmployeeId());
        record.setTaskId(taskId);
        record.setStartTime(request.getStartTime());
        record.setEndTime(request.getEndTime());
        record.setDescriptionWork(request.getDescriptionWork());
        timeRecordMapper.insert(record);
        return mapToResponse(record);
    }

    @Transactional(readOnly = true)
    public List<TimeRecordResponse> getTimeRecords(Long employeeId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        List<TimeRecord> records = timeRecordMapper.findByEmployeeIdAndPeriod(employeeId, start, end);
        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TimeRecordResponse mapToResponse(TimeRecord record) {
        TimeRecordResponse response = new TimeRecordResponse();
        response.setId(record.getId());
        response.setEmployeeId(record.getEmployeeId());
        response.setTaskId(record.getTaskId());
        response.setStartTime(record.getStartTime());
        response.setEndTime(record.getEndTime());
        response.setDescriptionWork(record.getDescriptionWork());
        return response;
    }
}