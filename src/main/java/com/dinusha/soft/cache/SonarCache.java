package com.dinusha.soft.cache;

import com.dinusha.soft.service.AnalysisService;
import com.dinusha.soft.service.SCMService;
import com.dinusha.soft.service.ViolationService;
import com.dinusha.soft.utills.JsonUtil;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Component
public class SonarCache {
    private static final String CACHE_PATH = "./cache/";
    @Autowired
    private ViolationService violationService;
    @Autowired
    private SCMService scmService;
    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private AnalysisService analysisService;

    public void checkViolationCache(String projectKey, String date) {
        String folderPAth = CACHE_PATH + projectKey + "/" + date;
        Path violationPath = Paths.get(folderPAth + "/violation.json");
        System.out.println("violation path " + Files.exists(violationPath));
        checkAnalysisCache(projectKey, date);
//        createAnalysisCache(projectKey, date);
    }

    public void checkSCMCache(String projectKey, String date) {
        String folderPAth = CACHE_PATH + projectKey + "/" + date;
        Path scmPath = Paths.get(folderPAth + "/scm.json");
        System.out.println("scm path " + Files.exists(scmPath));
    }

    public void createAnalysisCache(String projectKey, String date) {
        Map<String, Map<String, String>> branchesAnalysis = analysisService.getBranchesAnalysis(projectKey);
        JSONObject jsonObject = jsonUtil.mapToJsonObject.apply(branchesAnalysis);
        createCacheAnalysisFile(jsonObject.toJSONString(), projectKey, "branchAnalysis");
        System.out.println(jsonObject);
    }

    @SuppressWarnings("rawtypes")
    public void checkAnalysisCache(String projectKey, String date) {
        //api analysis
        Map<String, Map<String, String>> branchesAnalysis = analysisService.getBranchesAnalysis(projectKey);
        JSONObject jsonObject = jsonUtil.mapToJsonObject.apply(branchesAnalysis);
        JSONObject jsonBranchesAPI = new JSONObject();

        int branchesAPISize = 0;
        for (Object key : jsonObject.keySet()) {
            jsonBranchesAPI = new JSONObject((Map) jsonObject.get(key));
            branchesAPISize = jsonBranchesAPI.size();
        }

        //cache analysis
        String folderPAth = CACHE_PATH + projectKey + "/branchAnalysis.json";
        try {
            List<String> data = (Files.readAllLines(Paths.get(folderPAth)));
            StringBuilder analysisFileContent = new StringBuilder();
            data.forEach(analysisFileContent::append);

            JSONObject jsonProject = jsonUtil.stringToJsonObject.apply(analysisFileContent.toString());
            for (Object key : jsonProject.keySet()) {

                //if month is less than the api latest analysis month the return the cache if available. else get data from api and create a cached and return the cache file
                for (Object apiKey : jsonBranchesAPI.keySet()) {

                    DateTime uiTimestamp = new DateTime(date);
                    //get api date
                    String apiDate = (jsonBranchesAPI.get(apiKey).toString()).substring(0, 10);

                    DateTime apiTimestamp = new DateTime(apiDate);

                    jsonBranchesAPI.get(apiKey);

                    System.out.println("ui miliseconds " + uiTimestamp.getMillis());
                    System.out.println("api miliseconds " + apiTimestamp.getMillis());
//                    if (uiTimestamp.getMillis() <apiTimestamp.getMillis()) {
//                        return;
//                    }
                }

                //if reading for current month
//                JSONObject jsonBranches = (JSONObject) jsonProject.get(key);
//                if (jsonBranches.size() != branchesAPISize) {
//                    //create cache
//                } else {
//
//                    //get branch key fro api and check with cached file branch data time
//                    for (Object branchKey : jsonBranchesAPI.keySet()) {
//                        String cachedBranchTimestamp = jsonBranches.get(branchKey).toString();
//                        String apiBranchTimestamp = jsonBranchesAPI.get(branchKey).toString();
//                        if (cachedBranchTimestamp.equals(apiBranchTimestamp)) {
//                            //return cached file
//                        } else {
//                            //re write cache
//                        }
//
//                    }
//                }
//
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private String createViolationCache(String projectKey, String date) {
        //get json from api
        List<Object> violationList = violationService.getViolation.apply(projectKey, date);
        String jsonViolationArray = jsonUtil.listToJsonStringArray.apply(violationList);

        //save it into json file in the cache folder
        System.out.println(jsonViolationArray);
        createCacheFile(jsonViolationArray, projectKey, date, "violation");
        return jsonViolationArray;
    }

    private void createSCMCache(String projectKey, String date) {
        //get json from api
        List<Object> scmList = scmService.getCommits.apply(projectKey, date);
        String jsonSCMnArray = jsonUtil.listToJsonStringArray.apply(scmList);

        //save it into json file in the cache folder
        createCacheFile(jsonSCMnArray, projectKey, date, "scm");
        System.out.println(jsonSCMnArray);
    }

    private void createCacheFile(String json, String projectKey, String date, String fileName) {
        String folderPAth = CACHE_PATH + projectKey + "/" + date;

        try {
            Path path = Paths.get(folderPAth);
            Files.createDirectories(path);
            Path filePath = Paths.get(folderPAth + "/" + fileName + ".json");
            Files.write(filePath, json.getBytes(StandardCharsets.UTF_8));
            System.out.println("File created");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createCacheAnalysisFile(String json, String projectKey, String fileName) {
        String folderPAth = CACHE_PATH + projectKey;
        try {
            Path path = Paths.get(folderPAth);
            Files.createDirectories(path);
            Path filePath = Paths.get(folderPAth + "/" + fileName + ".json");
            Files.write(filePath, json.getBytes(StandardCharsets.UTF_8));
            System.out.println("File created");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
