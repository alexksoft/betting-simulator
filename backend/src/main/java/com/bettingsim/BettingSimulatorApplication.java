package com.bettingsim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BettingSimulatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(BettingSimulatorApplication.class, args);
    }
}
