package com.dinusha.soft.controller;

import com.dinusha.soft.service.SonarProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectController {

    @Autowired
    private SonarProjectService sonarProjectService;

    @GetMapping("/v1/projects")
    public void sonarProjects() {

//       new  SonarProjectService().PROJECTS.get();
        System.out.println(sonarProjectService.getProjects.get());
        System.out.println();
    }
}
