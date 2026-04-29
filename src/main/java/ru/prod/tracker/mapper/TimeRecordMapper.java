package ru.prod.tracker.mapper;

import ru.prod.tracker.model.TimeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TimeRecordMapper {
    int insert(TimeRecord timeRecord);
    List<TimeRecord> findByEmployeeIdAndPeriod(@Param("employeeId") Long employeeId,
                                               @Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);
}