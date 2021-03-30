package com.dinusha.soft.service;

import com.dinusha.soft.utills.JsonUtil;
import com.dinusha.soft.webclient.Client;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

@Service
public class SCM {

    BiFunction<String, String, Map<String, Integer>> COMMITS = (url, key) -> {

        /**TODO*/
        //given date
        String date = "2020-03";
        //commits count for a branch
        int commitCount = 0;

        Map<String, List<String>> sonarSources = new SonarFile().getFiles.apply("");

        HashMap<String, Integer> scmCommitCount = new HashMap<>();
        for (Map.Entry<String, List<String>> branch : sonarSources.entrySet()) {
            for (String src : branch.getValue()) {
                String commits = Client.GET.apply("http://localhost:9000/api/sources/scm?key=SonarQubeOpenViolationMonitor:" + src + "");
                JSONObject jsonCommits = JsonUtil.JSON_OBJECT.apply(commits);
                JSONArray scmArr = (JSONArray) jsonCommits.get("scm");
                if (Objects.nonNull(scmArr)) {
                    for (Object scmData : scmArr) {
                        JSONArray array = (JSONArray) scmData;

                        String smcDate = ((String) array.get(2)).substring(0, 7);
                        if (smcDate.equals(date)) {
                            commitCount += 1;
                        }
                    }
                }
            }
            scmCommitCount.put(branch.getKey(), commitCount);
            //set commit count to 0 after branch analyzes
            commitCount = 0;
        }
        return scmCommitCount;
    };
}
