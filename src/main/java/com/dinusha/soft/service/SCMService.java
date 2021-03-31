package com.dinusha.soft.service;

import com.dinusha.soft.utills.JsonUtil;
import com.dinusha.soft.webclient.Client;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

@Service
public class SCMService {
    private static final Logger logger = Logger.getLogger(SCMService.class);
    @Autowired
    private SonarFileService sonarFileService;
    @Value("${sonar.host}")
    private String host;

    public BiFunction<String, String, Map<String, Integer>> getCommits = (projectKey, date) -> {

        //commits count for a branch
        int commitCount = 0;

        Map<String, List<String>> sonarSources = sonarFileService.getFiles.apply(projectKey);
        HashMap<String, Integer> scmCommitCount = new HashMap<>();

        logger.info("Reading sources in SonarQube branches");
        for (Map.Entry<String, List<String>> branch : sonarSources.entrySet()) {
            for (String src : branch.getValue()) {
                logger.debug("Reading sources for in branch : " + branch.getKey() + " : " + src);
                String commits = Client.GET.apply(host + "api/sources/scm?key=" + projectKey + ":" + src + "");
                JSONObject jsonCommits = JsonUtil.JSON_OBJECT.apply(commits);
                JSONArray scmArr = (JSONArray) jsonCommits.get("scm");
                if (Objects.nonNull(scmArr)) {
                    for (Object scmData : scmArr) {
                        JSONArray array = (JSONArray) scmData;
                        String smcDate = ((String) array.get(2)).substring(0, 7);
                        if (smcDate.equals(date)) {
                            commitCount += 1;
                            logger.debug("Branch : " + branch.getKey() + " commits count incrementing " + commits + " for : " + date);
                        }
                    }
                }
            }
            scmCommitCount.put(branch.getKey(), commitCount);
            logger.debug("Adding SCM commits for branches : " + scmCommitCount);
            //set commit count to 0 after branch analyzes
            commitCount = 0;
        }
        logger.info("Reading sources in SonarQube branches completed!");
        return scmCommitCount;
    };
}
