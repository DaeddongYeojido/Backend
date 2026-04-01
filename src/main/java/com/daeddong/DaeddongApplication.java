package com.daeddong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DaeddongApplication {
    public static void main(String[] args) {
        SpringApplication.run(DaeddongApplication.class, args);
    }
}
