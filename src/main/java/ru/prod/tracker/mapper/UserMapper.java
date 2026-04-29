package ru.prod.tracker.mapper;

import ru.prod.tracker.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findByUsername(String username);
}