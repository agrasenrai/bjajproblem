package com.bajaj.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class NthLevelFollowersApplication {

    public static void main(String[] args) {
        SpringApplication.run(NthLevelFollowersApplication.class, args);
    }
} 