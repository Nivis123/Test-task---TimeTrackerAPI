package ru.prod.tracker.service;

import ru.prod.tracker.dto.TaskCreateRequest;
import ru.prod.tracker.dto.TaskResponse;
import ru.prod.tracker.dto.TaskStatusUpdateRequest;
import ru.prod.tracker.exception.ResourceNotFoundException;
import ru.prod.tracker.mapper.TaskMapper;
import ru.prod.tracker.model.Task;
import ru.prod.tracker.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTaskShouldInsertAndReturnTask() {
        TaskCreateRequest request = new TaskCreateRequest();
        request.setName("Test task");
        request.setDescription("Desc");

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        doAnswer(invocation -> {
            Task t = invocation.getArgument(0);
            t.setId(1L);
            return 1;
        }).when(taskMapper).insert(taskCaptor.capture());

        TaskResponse response = taskService.createTask(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Test task");
        assertThat(response.getStatus()).isEqualTo(TaskStatus.NEW);
        Task captured = taskCaptor.getValue();
        assertThat(captured.getStatus()).isEqualTo(TaskStatus.NEW);
    }

    @Test
    void getTaskShouldReturnWhenFound() {
        Task task = new Task();
        task.setId(1L);
        task.setName("T");
        task.setStatus(TaskStatus.NEW);
        when(taskMapper.findById(1L)).thenReturn(task);

        TaskResponse response = taskService.getTask(1L);
        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    void getTaskShouldThrowWhenNotFound() {
        when(taskMapper.findById(99L)).thenReturn(null);
        assertThatThrownBy(() -> taskService.getTask(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateStatusShouldUpdateAndReturn() {
        Task task = new Task();
        task.setId(2L);
        task.setStatus(TaskStatus.NEW);
        when(taskMapper.findById(2L)).thenReturn(task);

        TaskStatusUpdateRequest update = new TaskStatusUpdateRequest();
        update.setStatus(TaskStatus.IN_PROGRESS);

        TaskResponse response = taskService.updateStatus(2L, update);

        verify(taskMapper).updateStatus(2L, TaskStatus.IN_PROGRESS);
        assertThat(response.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }
}