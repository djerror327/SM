package com.dinusha.soft.controller;

import com.dinusha.soft.service.ViolationService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ViolationController {
    private static final Logger logger = Logger.getLogger(ViolationController.class);
    @Autowired
    private ViolationService violationService;

    @GetMapping("/v1/violations/{projectKey}/{date}")
    public Map<String, Integer> getViolations(@PathVariable String projectKey, @PathVariable String date) {
        logger.debug("GET : /v1/violations/" + projectKey + "/" + date);
        return violationService.getViolation.apply(projectKey, date);
    }
}
