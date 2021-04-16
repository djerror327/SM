package com.dinusha.soft.service;

import com.dinusha.soft.utills.JsonUtil;
import com.dinusha.soft.utills.SonarAuthHeader;
import com.dinusha.soft.webclient.Client;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisService {

    @Autowired
    private Client client;
    @Autowired
    private SonarAuthHeader sonarAuthHeader;
    @Autowired
    private BranchService branchService;
    @Autowired
    private JsonUtil jsonUtil;
    @Value("${sonar.host}")
    private String host;


    public Map<String, Map<String, String>> getBranchesAnalysis(String projectKey) {
        List<String> branchList = branchService.getBranches.apply(projectKey);
        Map<String, String> branchAnalysis = new HashMap<>();
        for (String branch : branchList) {
            String jsonString = checkLastAnalysis(host, projectKey, branch);
            JSONObject jsonObj = jsonUtil.stringToJsonObject.apply(jsonString);
            String lastAnalysis = ((String) jsonObj.get("analysisDate")).substring(0, 19);
            branchAnalysis.put(branch, lastAnalysis);
        }
        Map<String, Map<String, String>> branchesAnalysis = new HashMap<>();
        branchesAnalysis.put(projectKey, branchAnalysis);
        return branchesAnalysis;
    }

    public String checkLastAnalysis(String host, String projectKey, String branch) {
        return client.getWithAuthHeader.apply(sonarAuthHeader.authHeader.get(), host + "api/navigation/component?component=" + projectKey + "&branch=" + branch);
    }
}
