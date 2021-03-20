package com.dinusha.soft;

import com.dinusha.soft.service.BranchService;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppStarter {
    private static final Logger logger = Logger.getLogger(AppStarter.class);

    public static void main(String[] args) {
        logger.info("App Starting");
        SpringApplication.run(AppStarter.class, args);

        System.out.println(BranchService.GET_LIST.get());
        System.out.println();
    }
}
