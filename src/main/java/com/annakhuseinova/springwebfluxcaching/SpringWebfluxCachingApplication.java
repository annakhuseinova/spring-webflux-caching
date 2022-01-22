package com.annakhuseinova.springwebfluxcaching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SpringWebfluxCachingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringWebfluxCachingApplication.class, args);
    }

}
