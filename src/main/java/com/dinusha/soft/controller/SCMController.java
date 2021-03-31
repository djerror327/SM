package com.dinusha.soft.controller;

import com.dinusha.soft.service.SCMService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SCMController {
    private static final Logger logger = Logger.getLogger(SCMController.class);
    @Autowired
    private SCMService scmService;

    @GetMapping("/v1/scm/commits/{projectKey}/{date}")
    public Map<String, Integer> getCommits(@PathVariable String projectKey, @PathVariable String date) {
        logger.debug("GET : /v1/scm/commits/" + projectKey + "/" + date);
        return scmService.getCommits.apply(projectKey, date);
    }
}
