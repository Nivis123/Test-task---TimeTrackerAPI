package ru.prod.tracker.mapper;

import ru.prod.tracker.model.Task;
import ru.prod.tracker.model.TaskStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TaskMapper {
    int insert(Task task);
    Task findById(Long id);
    int updateStatus(@Param("id") Long id, @Param("status") TaskStatus status);
}