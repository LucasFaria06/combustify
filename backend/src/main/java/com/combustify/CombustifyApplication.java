package com.combustify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CombustifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(CombustifyApplication.class, args);
    }

}
