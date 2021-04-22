package com.dinusha.soft.controller;

import com.dinusha.soft.cache.SCMCache;
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
    private SCMCache scmCache;

    @GetMapping("/v1/scm/commits/{projectKey}/{date}")
    public String getCommits(@PathVariable String projectKey, @PathVariable String date) {
        logger.debug("GET : /v1/scm/commits/" + projectKey + "/" + date);
        Map<String, String> cacheData = scmCache.checkCacheEnableSCM.apply(projectKey, date);
        return cacheData.get("scm");
    }
}
