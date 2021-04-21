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
import java.util.Objects;
import java.util.function.*;

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

    private final BinaryOperator<String> createViolationCache = (projectKey, date) -> {
        //get json from api
        List<Object> violationList = violationService.getViolation.apply(projectKey, date);
        String jsonViolationArray = jsonUtil.listToJsonStringArray.apply(violationList);

        //save it into json file in the cache folder
        System.out.println(jsonViolationArray);
        createCacheFile(jsonViolationArray, projectKey, date, "violation");
        return jsonViolationArray;
    };

    private final BinaryOperator<String> createSCMCache = (projectKey, date) -> {
        //get json from api
        List<Object> scmList = scmService.getCommits.apply(projectKey, date);
        String jsonSCMnArray = jsonUtil.listToJsonStringArray.apply(scmList);

        //save it into json file in the cache folder
        createCacheFile(jsonSCMnArray, projectKey, date, "scm");
        return jsonSCMnArray;
    };
    private final BiConsumer<String, String> createCacheAnalysisFile = (json, projectKey) -> {
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
    };
    private final Consumer<String> createBranchAnalysisCacheJson = projectKey -> {
        Map<String, Map<String, String>> branchesAnalysis = analysisService.getBranchesAnalysis(projectKey);
        JSONObject jsonObject = jsonUtil.mapToJsonObject.apply(branchesAnalysis);
        createCacheAnalysisFile.accept(jsonObject.toJSONString(), projectKey);
        System.out.println(jsonObject);
    };

    private final BooleanSupplier checkCacheFolderExist = () -> Files.isDirectory(Paths.get(CACHE_PATH));

    private final BiPredicate<String, String> checkViolationCache = (String projectKey, String date) -> {
        String folderPAth = CACHE_PATH + projectKey + "/" + date;
        Path violationPath = Paths.get(folderPAth + "/violation.json");
        return Files.exists(violationPath);
    };

    private final BiPredicate<String, String> checkSCMCache = (String projectKey, String date) -> {
        String folderPAth = CACHE_PATH + projectKey + "/" + date;
        Path scmPath = Paths.get(folderPAth + "/scm.json");
        System.out.println("scm path " + Files.exists(scmPath));
        return Files.exists(scmPath);
    };
    private final Function<String, StringBuilder> readCacheFile = folderPAth -> {
        List<String> data = null;
        try {
            data = (Files.readAllLines(Paths.get(folderPAth)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder analysisFileContent = new StringBuilder();
        Objects.requireNonNull(data).forEach(analysisFileContent::append);
        System.out.println("JSON FILE CONTENT FOR STRING BUILDER :" + analysisFileContent);
        return analysisFileContent;
    };
    private final BiFunction<String, String, Map<String, Long>> getMillis = (date, apiDate) -> {
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
    };
    private final BiFunction<String, String, Map<String, String>> getCachedData = (violation, scm) -> {
        Map<String, String> cachedFileData = new HashMap<>();
        cachedFileData.put("violation", violation);
        cachedFileData.put("scm", scm);
        return cachedFileData;
    };
    @SuppressWarnings("rawtypes")
    public final BiFunction<String, String, Map<String, String>> checkAnalysisCache = (projectKey, date) -> {
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
        if (checkCacheFolderExist.getAsBoolean()) {
            StringBuilder analysisFileContent = readCacheFile.apply(folderPAth);

            JSONObject cachedJsonProject = jsonUtil.stringToJsonObject.apply(analysisFileContent.toString());
            for (Object key : cachedJsonProject.keySet()) {

                //if month is less than the api latest analysis month the return the cache if available. else get data from api and create a cached and return the cache file
                for (Object apiKey : jsonBranchesAPI.keySet()) {

                    //get api date
                    String apiDate = (jsonBranchesAPI.get(apiKey).toString()).substring(0, 10);

                    //convert to 1st of the current date od api for ui match
                    Map<String, Long> timestamp = getMillis.apply(date, apiDate);

                    //check cache folder is exist. if not create cache
                    //if date is smaller than api then return cache if not exist then create cache
                    if (timestamp.get("dateMillis") < timestamp.get("apiMillis")) {

                        //check cache available the return else create cache and return

                        if (checkViolationCache.test(projectKey, date) && checkSCMCache.test(projectKey, date)) {
//                                Map<String, String> cachedFileData = new HashMap<>();
//                                cachedFileData.put("violation", String.valueOf(readCacheFile(violationPath)));
//                                cachedFileData.put("scm", String.valueOf(readCacheFile(scmPath)));

                            return getCachedData.apply(String.valueOf(readCacheFile.apply(violationPath)), String.valueOf(readCacheFile.apply(scmPath)));
                        } else {
                            //create branch analysis cache json
                            createBranchAnalysisCacheJson.accept(projectKey);
                            String violation = createViolationCache.apply(projectKey, date);
                            String scm = createSCMCache.apply(projectKey, date);
//                                Map<String, String> cachedFileData = new HashMap<>();
//                                cachedFileData.put("violation", violation);
//                                cachedFileData.put("scm", scm);

//                                //create branch analysis cache json
//                                createBranchAnalysisCacheJson(projectKey);

                            return getCachedData.apply(violation, scm);
                        }
                    } else if (timestamp.get("dateMillis").equals(timestamp.get("apiMillis"))) {
                        //if reading for current month
                        JSONObject cachedJsonBranches = (JSONObject) cachedJsonProject.get(key);
                        if (cachedJsonBranches.size() != branchesAPISize) {
                            //create cache
                            //create branch analysis cache json
                            createBranchAnalysisCacheJson.accept(projectKey);
                            String violation = createViolationCache.apply(projectKey, date);
                            String scm = createSCMCache.apply(projectKey, date);
//                                Map<String, String> cachedFileData = new HashMap<>();
//                                cachedFileData.put("violation", violation);
//                                cachedFileData.put("scm", scm);

                            //create branch analysis cache json
//                                createBranchAnalysisCacheJson(projectKey);
                            return getCachedData.apply(violation, scm);
//                                return cachedFileData;
                        } else {
                            //check api branch analysis and cached jason file timestamp. if there is time mismatch then recreate cache
                            for (Object cachedKey : cachedJsonBranches.keySet()) {
                                String cachedDate = (cachedJsonBranches.get(cachedKey).toString()).substring(0, 10);
                                String apiDateCurrentMonth = (jsonBranchesAPI.get(cachedKey).toString()).substring(0, 10);
                                Map<String, Long> millis = getMillis.apply(cachedDate, apiDateCurrentMonth);
                                if (millis.get("dateMillis") < (millis.get("apiMillis"))) {
                                    //create branch analysis cache json
                                    createBranchAnalysisCacheJson.accept(projectKey);
                                    String violation = createViolationCache.apply(projectKey, date);
                                    String scm = createSCMCache.apply(projectKey, date);
//                                        Map<String, String> cachedFileData = new HashMap<>();
//                                        cachedFileData.put("violation", violation);
//                                        cachedFileData.put("scm", scm);

                                    //create branch analysis cache json
//                                        createBranchAnalysisCacheJson(projectKey);
                                    return getCachedData.apply(violation, scm);
//                                        return cachedFileData;
                                }

                            }

                            if (checkViolationCache.test(projectKey, date) && checkSCMCache.test(projectKey, date)) {
//                                    Map<String, String> cachedFileData = new HashMap<>();
//                                    cachedFileData.put("violation", String.valueOf(readCacheFile(violationPath)));
//                                    cachedFileData.put("scm", String.valueOf(readCacheFile(scmPath)));
//                                    System.out.println("debug cache file data : " + cachedFileData);
//                                    System.out.println("debug cache file data get violation : " + cachedFileData.get("violation"));
                                return getCachedData.apply(String.valueOf(readCacheFile.apply(violationPath)), String.valueOf(readCacheFile.apply(scmPath)));
//                                    return cachedFileData;
                            }
                            //if cache file not available for current month
                            else {
                                //create branch analysis cache json
                                createBranchAnalysisCacheJson.accept(projectKey);
                                String violation = createViolationCache.apply(projectKey, date);
                                String scm = createSCMCache.apply(projectKey, date);
//                                    Map<String, String> cachedFileData = new HashMap<>();
//                                    cachedFileData.put("violation", violation);
//                                    cachedFileData.put("scm", scm);

                                //create branch analysis cache json
//                                    createBranchAnalysisCacheJson(projectKey);

                                return getCachedData.apply(violation, scm);
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
                        return getCachedData.apply(jsonArrViolation.toJSONString(), jsonArrSCM.toJSONString());
//                            return cachedFileData;
                    }
                }
            }
        } else {
            //create branch analysis cache json
            createBranchAnalysisCacheJson.accept(projectKey);
            //if cache folder is not available re create cache
//                Map<String, String> cachedFileData = new HashMap<>();
            String violation = createViolationCache.apply(projectKey, date);
            String scm = createSCMCache.apply(projectKey, date);
//
//                cachedFileData.put("violation", violation);
//                cachedFileData.put("scm", scm);


            return getCachedData.apply(violation, scm);
//                return cachedFileData;
        }
        return null;
    };

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


}
