package ru.prod.tracker.service;

import ru.prod.tracker.dto.TaskCreateRequest;
import ru.prod.tracker.dto.TaskResponse;
import ru.prod.tracker.dto.TaskStatusUpdateRequest;
import ru.prod.tracker.exception.ResourceNotFoundException;
import ru.prod.tracker.mapper.TaskMapper;
import ru.prod.tracker.model.Task;
import ru.prod.tracker.model.TaskStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final TaskMapper taskMapper;

    public TaskService(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Transactional
    public TaskResponse createTask(TaskCreateRequest request) {
        Task task = new Task();
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setStatus(TaskStatus.NEW);
        taskMapper.insert(task);
        return mapToResponse(task);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(Long id) {
        Task task = taskMapper.findById(id);
        if (task == null) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }
        return mapToResponse(task);
    }

    @Transactional
    public TaskResponse updateStatus(Long id, TaskStatusUpdateRequest request) {
        Task task = taskMapper.findById(id);
        if (task == null) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }
        taskMapper.updateStatus(id, request.getStatus());
        task.setStatus(request.getStatus());
        return mapToResponse(task);
    }

    private TaskResponse mapToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setName(task.getName());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        return response;
    }
}