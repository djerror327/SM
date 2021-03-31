package com.dinusha.soft.controller;

import com.dinusha.soft.service.SonarProjectService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProjectController {
    private static final Logger logger = Logger.getLogger(ProjectController.class);
    @Autowired
    private SonarProjectService sonarProjectService;

    @GetMapping("/v1/projects")
    public List<String> sonarProjects() {
        logger.debug("GET : /v1/projects");
        return sonarProjectService.getProjects.get();
    }
}
