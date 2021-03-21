package com.dinusha.soft;

import com.dinusha.soft.service.ViolationService;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppStarter {
    private static final Logger logger = Logger.getLogger(AppStarter.class);

    public static void main(String[] args) {
        logger.info("App Starting");
        SpringApplication.run(AppStarter.class, args);

        System.out.println(ViolationService.GET_VIOLATION.apply(""));
        System.out.println();
    }
}
