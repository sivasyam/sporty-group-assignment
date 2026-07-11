package com.sporty.assignment;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableKafka
public class SportyGroupAssignmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(SportyGroupAssignmentApplication.class, args);
    }
}
