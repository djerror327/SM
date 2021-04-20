package com.dinusha.soft.cache;

import com.dinusha.soft.service.AnalysisService;
import com.dinusha.soft.service.SCMService;
import com.dinusha.soft.service.ViolationService;
import com.dinusha.soft.utills.JsonUtil;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.json.simple.JSONArray;
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
import java.util.function.BooleanSupplier;

@Component
public class SonarCache {

    private static final Logger logger = Logger.getLogger(SonarCache.class);

    private static final String CACHE_PATH = "./cache/";

    @Autowired
    private ViolationService violationService;
    @Autowired
    private SCMService scmService;
    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private AnalysisService analysisService;


    public final BooleanSupplier deleteAllCache = () -> {
        try {
            logger.info("Deleting cache folder");
            return Files.deleteIfExists(Paths.get(CACHE_PATH));
        } catch (IOException e) {
            logger.error(e.getStackTrace());
        }
        logger.warn("Cache folder is not deleted!");
        return false;
    };


    public boolean checkCacheFolderExist() throws IOException {
        return Files.isDirectory(Paths.get(CACHE_PATH));
    }

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

    public void createBranchAnalysisCacheJson(String projectKey) {
        Map<String, Map<String, String>> branchesAnalysis = analysisService.getBranchesAnalysis(projectKey);
        JSONObject jsonObject = jsonUtil.mapToJsonObject.apply(branchesAnalysis);
        createCacheAnalysisFile(jsonObject.toJSONString(), projectKey);
        System.out.println(jsonObject);
    }

    public StringBuilder readCacheFile(String folderPAth) throws IOException {
        List<String> data = (Files.readAllLines(Paths.get(folderPAth)));
        StringBuilder analysisFileContent = new StringBuilder();
        data.forEach(analysisFileContent::append);
        System.out.println("JSON FILE CONTENT FOR STRING BUILDER :" + analysisFileContent);
        return analysisFileContent;
    }

    private Map<String, Long> getMillis(String date, String apiDate) {
        DateTime cachedTimestamp = new DateTime(date);
        DateTime apiTimestamp = new DateTime(apiDate);

        //convert to 1st of the current date od api for ui match
        int apiYaer = apiTimestamp.getYear();
        int apiMonth = apiTimestamp.getMonthOfYear();
        apiTimestamp = new DateTime(apiYaer + "-" + apiMonth);
        HashMap<String, Long> timestamp = new HashMap<>();
        timestamp.put("dateMillis", cachedTimestamp.getMillis());
        timestamp.put("apiMillis", apiTimestamp.getMillis());
        return timestamp;
    }

    private Map<String, String> getCachedData(String violation, String scm) {
        Map<String, String> cachedFileData = new HashMap<>();
        cachedFileData.put("violation", violation);
        cachedFileData.put("scm", scm);
        return cachedFileData;
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
//        //cache analysis
        String folderPAth = CACHE_PATH + projectKey + "/branchAnalysis.json";
        String violationPath = CACHE_PATH + projectKey + "/" + date + "/" + "violation.json";
        String scmPath = CACHE_PATH + projectKey + "/" + date + "/" + "scm.json";
        try {
            if (checkCacheFolderExist()) {
                StringBuilder analysisFileContent = readCacheFile(folderPAth);

                JSONObject cachedJsonProject = jsonUtil.stringToJsonObject.apply(analysisFileContent.toString());
                for (Object key : cachedJsonProject.keySet()) {

                    //if month is less than the api latest analysis month the return the cache if available. else get data from api and create a cached and return the cache file
                    for (Object apiKey : jsonBranchesAPI.keySet()) {

                        //get api date
                        String apiDate = (jsonBranchesAPI.get(apiKey).toString()).substring(0, 10);

                        //convert to 1st of the current date od api for ui match
                        Map<String, Long> timestamp = getMillis(date, apiDate);

                        //check cache folder is exist. if not create cache
                        //if date is smaller than api then return cache if not exist then create cache
                        if (timestamp.get("dateMillis") < timestamp.get("apiMillis")) {

                            //check cache available the return else create cache and return

                            if (checkViolationCache(projectKey, date) && checkSCMCache(projectKey, date)) {
//                                Map<String, String> cachedFileData = new HashMap<>();
//                                cachedFileData.put("violation", String.valueOf(readCacheFile(violationPath)));
//                                cachedFileData.put("scm", String.valueOf(readCacheFile(scmPath)));

                                return getCachedData(String.valueOf(readCacheFile(violationPath)), String.valueOf(readCacheFile(scmPath)));
                            } else {
                                //create branch analysis cache json
                                createBranchAnalysisCacheJson(projectKey);
                                String violation = createViolationCache(projectKey, date);
                                String scm = createSCMCache(projectKey, date);
//                                Map<String, String> cachedFileData = new HashMap<>();
//                                cachedFileData.put("violation", violation);
//                                cachedFileData.put("scm", scm);

//                                //create branch analysis cache json
//                                createBranchAnalysisCacheJson(projectKey);

                                return getCachedData(violation, scm);
                            }
                        } else if (timestamp.get("dateMillis").equals(timestamp.get("apiMillis"))) {
                            //if reading for current month
                            JSONObject cachedJsonBranches = (JSONObject) cachedJsonProject.get(key);
                            if (cachedJsonBranches.size() != branchesAPISize) {
                                //create cache
                                //create branch analysis cache json
                                createBranchAnalysisCacheJson(projectKey);
                                String violation = createViolationCache(projectKey, date);
                                String scm = createSCMCache(projectKey, date);
//                                Map<String, String> cachedFileData = new HashMap<>();
//                                cachedFileData.put("violation", violation);
//                                cachedFileData.put("scm", scm);

                                //create branch analysis cache json
//                                createBranchAnalysisCacheJson(projectKey);
                                return getCachedData(violation, scm);
//                                return cachedFileData;
                            } else {
                                //check api branch analysis and cached jason file timestamp. if there is time mismatch then recreate cache
                                for (Object cachedKey : cachedJsonBranches.keySet()) {
                                    String cachedDate = (cachedJsonBranches.get(cachedKey).toString()).substring(0, 10);
                                    String apiDateCurrentMonth = (jsonBranchesAPI.get(cachedKey).toString()).substring(0, 10);
                                    Map<String, Long> millis = getMillis(cachedDate, apiDateCurrentMonth);
                                    if (millis.get("dateMillis") < (millis.get("apiMillis"))) {
                                        //create branch analysis cache json
                                        createBranchAnalysisCacheJson(projectKey);
                                        String violation = createViolationCache(projectKey, date);
                                        String scm = createSCMCache(projectKey, date);
//                                        Map<String, String> cachedFileData = new HashMap<>();
//                                        cachedFileData.put("violation", violation);
//                                        cachedFileData.put("scm", scm);

                                        //create branch analysis cache json
//                                        createBranchAnalysisCacheJson(projectKey);
                                        return getCachedData(violation, scm);
//                                        return cachedFileData;
                                    }

                                }

                                if (checkViolationCache(projectKey, date) && checkSCMCache(projectKey, date)) {
//                                    Map<String, String> cachedFileData = new HashMap<>();
//                                    cachedFileData.put("violation", String.valueOf(readCacheFile(violationPath)));
//                                    cachedFileData.put("scm", String.valueOf(readCacheFile(scmPath)));
//                                    System.out.println("debug cache file data : " + cachedFileData);
//                                    System.out.println("debug cache file data get violation : " + cachedFileData.get("violation"));
                                    return getCachedData(String.valueOf(readCacheFile(violationPath)), String.valueOf(readCacheFile(scmPath)));
//                                    return cachedFileData;
                                }
                                //if cache file not available for current month
                                else {
                                    //create branch analysis cache json
                                    createBranchAnalysisCacheJson(projectKey);
                                    String violation = createViolationCache(projectKey, date);
                                    String scm = createSCMCache(projectKey, date);
//                                    Map<String, String> cachedFileData = new HashMap<>();
//                                    cachedFileData.put("violation", violation);
//                                    cachedFileData.put("scm", scm);

                                    //create branch analysis cache json
//                                    createBranchAnalysisCacheJson(projectKey);

                                    return getCachedData(violation, scm);
                                    //re write cache
                                }
                            }
                        }
                        //ui timestamp is greater than api timestamp
                        else {
                            List<Object> violationAPI = violationService.getViolation.apply(projectKey, date);
                            List<Object> scmAPI = scmService.getCommits.apply(projectKey, date);
//                            Map<String, String> cachedFileData = new HashMap<>();

                            JSONArray jsonArrViolation = jsonUtil.listToJsonArray.apply(violationAPI);
                            JSONArray jsonArrSCM = jsonUtil.listToJsonArray.apply(scmAPI);

//                            cachedFileData.put("violation", jsonArrViolation.toJSONString());
//                            cachedFileData.put("scm", jsonArrSCM.toJSONString());

//                            //create cache file
//                            createAnalysisCache(projectKey);
                            return getCachedData(jsonArrViolation.toJSONString(), jsonArrSCM.toJSONString());
//                            return cachedFileData;
                        }
                    }
                }
            } else {
                //create branch analysis cache json
                createBranchAnalysisCacheJson(projectKey);
                //if cache folder is not available re create cache
//                Map<String, String> cachedFileData = new HashMap<>();
                String violation = createViolationCache(projectKey, date);
                String scm = createSCMCache(projectKey, date);
//
//                cachedFileData.put("violation", violation);
//                cachedFileData.put("scm", scm);


                return getCachedData(violation, scm);
//                return cachedFileData;
            }
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

    private void createCacheAnalysisFile(String json, String projectKey) {
        String folderPAth = CACHE_PATH + projectKey;
        try {
            Path path = Paths.get(folderPAth);
            Files.createDirectories(path);
            Path filePath = Paths.get(folderPAth + "/" + "branchAnalysis.json");
            Files.write(filePath, json.getBytes(StandardCharsets.UTF_8));
            System.out.println("File created");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
