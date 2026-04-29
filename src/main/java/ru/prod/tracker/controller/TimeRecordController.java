package ru.prod.tracker.controller;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.prod.tracker.dto.TimeRecordCreateRequest;
import ru.prod.tracker.dto.TimeRecordResponse;
import ru.prod.tracker.service.TimeRecordService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TimeRecordController {

    private final TimeRecordService timeRecordService;

    public TimeRecordController(TimeRecordService timeRecordService) {
        this.timeRecordService = timeRecordService;
    }

    @PostMapping("/tasks/{taskId}/timerecords")
    public ResponseEntity<TimeRecordResponse> createTimeRecord(@PathVariable Long taskId,
                                                               @Valid @RequestBody TimeRecordCreateRequest request) {
        TimeRecordResponse response = timeRecordService.createTimeRecord(taskId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/timerecords")
    public ResponseEntity<List<TimeRecordResponse>> getTimeRecords(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<TimeRecordResponse> records = timeRecordService.getTimeRecords(employeeId, startDate, endDate);
        return ResponseEntity.ok(records);
    }
}