package com.dinusha.soft.controller;

import com.dinusha.soft.cache.SonarCache;
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
    @Autowired
    private SonarCache sonarCache;

    @GetMapping("/v1/violations/{projectKey}/{date}")
    public String getViolations(@PathVariable String projectKey, @PathVariable String date) {
        logger.debug("GET : /v1/violations/" + projectKey + "/" + date);
//        sonarCache.createViolationCache(projectKey, date);
        Map<String, String> cacheData = sonarCache.checkAnalysisCache(projectKey, date);
//        List<Object> response = new ArrayList<>();
//        response.add(cacheData.get("violation"));
//        System.out.println("controller get violation : " + cacheData.get("violation"));
//        System.out.println("debug controller response : " + response);
//        System.out.println("debug controller realtime : " + violationService.getViolation.apply(projectKey, date));
        return cacheData.get("violation");

//        return violationService.getViolation.apply(projectKey, date);
//        return null;
    }
}
