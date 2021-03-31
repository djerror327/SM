package com.dinusha.soft.controller;

import com.dinusha.soft.service.SonarProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProjectController {

    @Autowired
    private SonarProjectService sonarProjectService;

    @GetMapping("/v1/projects")
    public List<String> sonarProjects() {
        return sonarProjectService.getProjects.get();
    }
}
