package ru.prod.tracker.dto;

import ru.prod.tracker.model.TaskStatus;
import jakarta.validation.constraints.NotNull;

public class TaskStatusUpdateRequest {
    @NotNull
    private TaskStatus status;

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
}