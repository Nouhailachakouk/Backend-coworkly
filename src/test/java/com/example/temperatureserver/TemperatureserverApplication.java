package com.example.temperatureserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TemperatureserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(TemperatureserverApplication.class, args);
    }

}
