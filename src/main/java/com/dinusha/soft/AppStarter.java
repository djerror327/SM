package com.dinusha.soft;

import com.dinusha.soft.service.SCM;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppStarter {
    private static final Logger logger = Logger.getLogger(AppStarter.class);

    public static void main(String[] args) {
        logger.info("App Starting");
        SpringApplication.run(AppStarter.class, args);
        SCM.getCommits();
//        System.out.println(SonarFile.GET_FILES.apply("", ""));
        System.out.println();
    }
}
