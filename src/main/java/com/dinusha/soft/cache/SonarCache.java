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
import java.util.HashMap;
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

    public boolean checkViolationCache(String projectKey, String date) {
        String folderPAth = CACHE_PATH + projectKey + "/" + date;
        Path violationPath = Paths.get(folderPAth + "/violation.json");
        return Files.exists(violationPath);
    }

    public boolean checkSCMCache(String projectKey, String date) {
        String folderPAth = CACHE_PATH + projectKey + "/" + date;
        Path scmPath = Paths.get(folderPAth + "/scm.json");
        System.out.println("scm path " + Files.exists(scmPath));
        return Files.exists(scmPath);
    }

    public void createAnalysisCache(String projectKey) {
        Map<String, Map<String, String>> branchesAnalysis = analysisService.getBranchesAnalysis(projectKey);
        JSONObject jsonObject = jsonUtil.mapToJsonObject.apply(branchesAnalysis);
        createCacheAnalysisFile(jsonObject.toJSONString(), projectKey, "branchAnalysis");
        System.out.println(jsonObject);
    }

    public StringBuilder readCacheFile(String folderPAth) throws IOException {
        List<String> data = (Files.readAllLines(Paths.get(folderPAth)));
        StringBuilder analysisFileContent = new StringBuilder();
        data.forEach(analysisFileContent::append);
        return analysisFileContent;
    }

    private Map<String, Long> getTimestamp(String uiDate, String apiDate) {
        DateTime uiTimestamp = new DateTime(uiDate);
        //get api date
//        String apiDate = (jsonBranchesAPI.get(apiKey).toString()).substring(0, 10);

        DateTime apiTimestamp = new DateTime(apiDate);

        //convert to 1st of the current date od api for ui match
        int apiYaer = apiTimestamp.getYear();
        int apiMonth = apiTimestamp.getMonthOfYear();
        apiTimestamp = new DateTime(apiYaer + "-" + apiMonth);
        HashMap<String, Long> timestamp = new HashMap<>();
        timestamp.put("uiTimestamp", uiTimestamp.getMillis());
        timestamp.put("apiTimestamp", apiTimestamp.getMillis());
        return timestamp;
    }

    @SuppressWarnings("rawtypes")
    public Map<String, String> checkAnalysisCache(String projectKey, String date) {
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
        String violationPath = CACHE_PATH + "/" + projectKey + "/" + date + "/" + "violation.json";
        String scmPath = CACHE_PATH + "/" + projectKey + "/" + date + "/" + "scm.json";
        try {

            StringBuilder analysisFileContent = readCacheFile(folderPAth);

            JSONObject jsonProject = jsonUtil.stringToJsonObject.apply(analysisFileContent.toString());
            for (Object key : jsonProject.keySet()) {

                //if month is less than the api latest analysis month the return the cache if available. else get data from api and create a cached and return the cache file
                for (Object apiKey : jsonBranchesAPI.keySet()) {

//                    DateTime uiTimestamp = new DateTime(date);
                    //get api date
                    String apiDate = (jsonBranchesAPI.get(apiKey).toString()).substring(0, 10);

//                    DateTime apiTimestamp = new DateTime(apiDate);

                    //convert to 1st of the current date od api for ui match
//                    int apiYaer = apiTimestamp.getYear();
//                    int apiMonth = apiTimestamp.getMonthOfYear();
//                    apiTimestamp = new DateTime(apiYaer + "-" + apiMonth);
                    Map<String, Long> timestamp = getTimestamp(date, apiDate);

//                    jsonBranchesAPI.get(apiKey);

                    System.out.println("ui milliseconds " + timestamp.get("uiTimestamp"));
                    System.out.println("api milliseconds " + timestamp.get("apiTimestamp"));

                    //if date is smaller than api then return cache if not exist then create cache
                    if (timestamp.get("uiTimestamp") < timestamp.get("apiTimestamp")) {

                        //check cache available the return else create cache and return
                        Map<String, String> cachedFileData;
                        if (checkViolationCache(projectKey, date) && checkSCMCache(projectKey, date)) {
                            cachedFileData = new HashMap<>();
                            cachedFileData.put("violation", String.valueOf(readCacheFile(violationPath)));
                            cachedFileData.put("scm", String.valueOf(readCacheFile(scmPath)));
                            return cachedFileData;
                        } else {
                            String violation = createViolationCache(projectKey, date);
                            String scm = createSCMCache(projectKey, date);
                            cachedFileData = new HashMap<>();
                            cachedFileData.put("violation", violation);
                            cachedFileData.put("scm", scm);

                            //create cache file
                            createAnalysisCache(projectKey);
                            return cachedFileData;
                        }
                    } else if (timestamp.get("uiTimestamp").equals(timestamp.get("apiTimestamp"))) {

                        //if reading for current month
                        JSONObject jsonBranches = (JSONObject) jsonProject.get(key);
                        Map<String, String> cachedFileData;
                        if (jsonBranches.size() != branchesAPISize) {
                            //create cache
                            String violation = createViolationCache(projectKey, date);
                            String scm = createSCMCache(projectKey, date);
                            cachedFileData = new HashMap<>();
                            cachedFileData.put("violation", violation);
                            cachedFileData.put("scm", scm);

                            //create cache file
                            createAnalysisCache(projectKey);
                            return cachedFileData;
                        } else {

                            //get branch key from api and check with cached file branch data time
//                        for (Object branchKey : jsonBranchesAPI.keySet()) {
//                            String cachedBranchTimestamp = jsonBranches.get(branchKey).toString();
//                            String apiBranchTimestamp = jsonBranchesAPI.get(branchKey).toString();
//                            if (cachedBranchTimestamp.equals(apiBranchTimestamp)) {
                            //return cached file
                            if (checkViolationCache(projectKey, date) && checkSCMCache(projectKey, date)) {
                                cachedFileData = new HashMap<>();
                                cachedFileData.put("violation", String.valueOf(readCacheFile(violationPath)));
                                cachedFileData.put("scm", String.valueOf(readCacheFile(scmPath)));
                                return cachedFileData;
                            }

                            //if cache file not available for current month
                            else {
                                String violation = createViolationCache(projectKey, date);
                                String scm = createSCMCache(projectKey, date);
                                cachedFileData = new HashMap<>();
                                cachedFileData.put("violation", violation);
                                cachedFileData.put("scm", scm);

                                //create cache file
                                createAnalysisCache(projectKey);
                                return cachedFileData;
                                //re write cache
                            }
                        }
                    }
                    //ui timestamp is greater than api timestamp
                    else {
                        Map<String, String> cachedFileData;
                        cachedFileData = new HashMap<>();
                        cachedFileData.put("violation", null);
                        cachedFileData.put("scm", null);

                        //create cache file
//                        createAnalysisCache(projectKey);
                        return cachedFileData;
                    }
                }
            }
//                }
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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

    private String createSCMCache(String projectKey, String date) {
        //get json from api
        List<Object> scmList = scmService.getCommits.apply(projectKey, date);
        String jsonSCMnArray = jsonUtil.listToJsonStringArray.apply(scmList);

        //save it into json file in the cache folder
        createCacheFile(jsonSCMnArray, projectKey, date, "scm");
        return jsonSCMnArray;
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
