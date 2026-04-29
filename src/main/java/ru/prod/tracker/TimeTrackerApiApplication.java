package ru.prod.tracker;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("ru.prod.tracker.mapper")
public class TimeTrackerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimeTrackerApiApplication.class, args);
    }

}
