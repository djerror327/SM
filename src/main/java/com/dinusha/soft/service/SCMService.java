package com.dinusha.soft.service;

import com.dinusha.soft.utills.JsonUtil;
import com.dinusha.soft.webclient.Client;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiFunction;

@Service
public class SCMService {
    private static final Logger logger = Logger.getLogger(SCMService.class);
    @Autowired
    private SonarFileService sonarFileService;
    @Value("${sonar.host}")
    private String host;

    public final BiFunction<String, String, List<Object>> getCommits = (projectKey, date) -> {

        //commits count for a branch
        int commitCount = 0;

        Map<String, List<String>> sonarSources = sonarFileService.getFiles.apply(projectKey);
        HashMap<String, Integer> scmCommitCount = new HashMap<>();
        HashMap<String, Integer> authorsMap = new HashMap<>();
        HashMap<String, HashMap<String, Integer>> authorsBranch = new HashMap<>();

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
                        String author = (String) array.get(1);
                        String smcDate = ((String) array.get(2)).substring(0, 7);
                        if (smcDate.equals(date)) {
                            //set authors commit count
                            if (authorsMap.containsKey(author)) {
                                //check author is available then increment commits one by one
                                int authorCommits = authorsMap.get(author);
                                authorsMap.put(author, ++authorCommits);
                            } else {
                                //if author is new add 1 commit initially
                                authorsMap.put(author, 1);
                            }
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
            //set authorsMap to new object after calculating for specific branch
            authorsBranch.put(branch.getKey(), authorsMap);
            authorsMap = new HashMap<>();
        }
        logger.info("Reading sources in SonarQube branches completed!");

        //creating final output
        HashMap<String, HashMap<String, Integer>> branches = new HashMap<>();
        branches.put("branch", scmCommitCount);
        HashMap<String, HashMap<String, HashMap<String, Integer>>> users = new HashMap<>();
        users.put("user", authorsBranch);
        List<Object> output = new ArrayList<>();
        output.add(branches);
        output.add(users);

        return output;
    };
}
