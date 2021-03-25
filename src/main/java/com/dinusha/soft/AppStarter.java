package com.dinusha.soft;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableAutoConfiguration
//@ComponentScan(basePackages = "com.dinusha.soft")
//@PropertySource("classpath:application.properties")
@SpringBootApplication
public class AppStarter {
    private static final Logger logger = Logger.getLogger(AppStarter.class);

    public static void main(String[] args) {
        logger.info("App Starting");
        SpringApplication.run(AppStarter.class, args);


//        SCM.getCommits();
//        System.out.println(SonarFile.GET_FILES.apply("", ""));
//        SonarProject.projectsList();
//        System.out.println(SonarProject.PROJECTS.get());
//        SonarAuthHeader.AUTH_HEADER.get();

//        System.out.println(BranchService.getBranches.apply("SonarQubeOpenViolationMonitor"));
    }
}
