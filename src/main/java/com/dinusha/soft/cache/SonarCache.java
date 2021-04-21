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
import org.springframework.beans.factory.annotation.Value;
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
    @SuppressWarnings("rawtypes")
    private final BiFunction<String, String, Map<String, String>> checkAnalysisCache = (projectKey, date) -> {
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
                    logger.debug("Removing API last analysis time and read only date");
                    //convert to 1st of the current date od api for ui match
                    Map<String, Long> timestamp = getMillis.apply(date, apiDate);
                    //check cache folder is exist. if not create cache
                    //if date is smaller than api then return cache if not exist then create cache
                    logger.debug("Checking date milliseconds are less than API milliseconds");
                    if (timestamp.get("dateMillis") < timestamp.get("apiMillis")) {
                        //check cache available the return else create cache and return
                        if (checkViolationCache.test(projectKey, date) && checkSCMCache.test(projectKey, date)) {
                            return getCachedData.apply(String.valueOf(readCacheFile.apply(violationPath)), String.valueOf(readCacheFile.apply(scmPath)));
                        } else {
                            //create branch analysis cache json
                            createBranchAnalysisCacheJson.accept(projectKey);
                            String violation = createViolationCache.apply(projectKey, date);
                            String scm = createSCMCache.apply(projectKey, date);
                            return getCachedData.apply(violation, scm);
                        }
                    } else if (timestamp.get("dateMillis").equals(timestamp.get("apiMillis"))) {
                        logger.debug("Checking date milliseconds and API milliseconds are equal");
                        //if reading for current month
                        JSONObject cachedJsonBranches = (JSONObject) cachedJsonProject.get(key);
                        if (cachedJsonBranches.size() != branchesAPISize) {
                            logger.debug("Checking cached json branch and API branches sizes are different");
                            //create cache
                            //create branch analysis cache json
                            createBranchAnalysisCacheJson.accept(projectKey);
                            String violation = createViolationCache.apply(projectKey, date);
                            String scm = createSCMCache.apply(projectKey, date);
                            //create branch analysis cache json
                            return getCachedData.apply(violation, scm);
                        } else {
                            //check api branch analysis and cached jason file timestamp. if there is time mismatch then recreate cache
                            for (Object cachedKey : cachedJsonBranches.keySet()) {
                                String cachedDate = (cachedJsonBranches.get(cachedKey).toString()).substring(0, 10);
                                String apiDateCurrentMonth = (jsonBranchesAPI.get(cachedKey).toString()).substring(0, 10);
                                Map<String, Long> millis = getMillis.apply(cachedDate, apiDateCurrentMonth);
                                if (millis.get("dateMillis") < (millis.get("apiMillis"))) {
                                    logger.debug("Checking date milliseconds are less than API milliseconds for current month");
                                    //create branch analysis cache json
                                    createBranchAnalysisCacheJson.accept(projectKey);
                                    String violation = createViolationCache.apply(projectKey, date);
                                    String scm = createSCMCache.apply(projectKey, date);
                                    //create branch analysis cache json
                                    return getCachedData.apply(violation, scm);
                                }

                            }
                            if (checkViolationCache.test(projectKey, date) && checkSCMCache.test(projectKey, date)) {
                                logger.debug("Checking cache data is available for current month. return cached data");
                                return getCachedData.apply(String.valueOf(readCacheFile.apply(violationPath)), String.valueOf(readCacheFile.apply(scmPath)));
                            }
                            //if cache file not available for current month
                            else {
                                //create branch analysis cache json
                                logger.debug("No cache data is available for current month. creating cache for current month");
                                createBranchAnalysisCacheJson.accept(projectKey);
                                String violation = createViolationCache.apply(projectKey, date);
                                String scm = createSCMCache.apply(projectKey, date);
                                //create branch analysis cache json
                                return getCachedData.apply(violation, scm);
                            }
                        }
                    }
                    //ui timestamp is greater than api timestamp
                    else {
                        logger.debug("Scanning for future date. Directly reading from API");
                        List<Object> violationAPI = violationService.getViolation.apply(projectKey, date);
                        List<Object> scmAPI = scmService.getCommits.apply(projectKey, date);
                        JSONArray jsonArrViolation = jsonUtil.listToJsonArray.apply(violationAPI);
                        JSONArray jsonArrSCM = jsonUtil.listToJsonArray.apply(scmAPI);
                        //create cache file
                        return getCachedData.apply(jsonArrViolation.toJSONString(), jsonArrSCM.toJSONString());
                    }
                }
            }
        } else {
            logger.debug("Cache folder is not available.");
            //create branch analysis cache json
            createBranchAnalysisCacheJson.accept(projectKey);
            //if cache folder is not available re create cache
            String violation = createViolationCache.apply(projectKey, date);
            String scm = createSCMCache.apply(projectKey, date);
            return getCachedData.apply(violation, scm);
        }
        return null;
    };


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
        logger.info("Creating violation cache : project key : " + projectKey + " : date : " + date);
        logger.debug("Writing into violation cache : " + jsonViolationArray);
        createCacheFile(jsonViolationArray, projectKey, date, "violation");
        return jsonViolationArray;
    };

    private final BinaryOperator<String> createSCMCache = (projectKey, date) -> {
        //get json from api
        List<Object> scmList = scmService.getCommits.apply(projectKey, date);
        String jsonSCMnArray = jsonUtil.listToJsonStringArray.apply(scmList);

        //save it into json file in the cache folder
        logger.info("Creating SCM cache : project key : " + projectKey + " : date : " + date);
        logger.debug("Writing into SCM cache : " + jsonSCMnArray);
        createCacheFile(jsonSCMnArray, projectKey, date, "scm");
        return jsonSCMnArray;
    };

    private final BiConsumer<String, String> createCacheAnalysisFile = (json, projectKey) -> {
        String folderPAth = CACHE_PATH + projectKey;
        try {
            Path path = Paths.get(folderPAth);
            Files.createDirectories(path);
            Path filePath = Paths.get(folderPAth + "/" + "branchAnalysis.json");
            logger.info("Creating branchAnalysis.json : project key : " + projectKey);
            Files.write(filePath, json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error(e.getStackTrace());
        }
    };

    private final Consumer<String> createBranchAnalysisCacheJson = projectKey -> {
        Map<String, Map<String, String>> branchesAnalysis = analysisService.getBranchesAnalysis(projectKey);
        JSONObject jsonObject = jsonUtil.mapToJsonObject.apply(branchesAnalysis);
        logger.debug("Creating Branch Analysis cache data : project key : " + projectKey);
        createCacheAnalysisFile.accept(jsonObject.toJSONString(), projectKey);
    };

    private final BooleanSupplier checkCacheFolderExist = () -> {
        logger.debug("Checking cache folder exist");
        return Files.isDirectory(Paths.get(CACHE_PATH));
    };

    private final BiPredicate<String, String> checkViolationCache = (String projectKey, String date) -> {
        String folderPAth = CACHE_PATH + projectKey + "/" + date;
        Path violationPath = Paths.get(folderPAth + "/violation.json");
        logger.debug("Checking violation cache json exist : projectKey: " + projectKey + " +date : " + date + " path : " + violationPath);
        return Files.exists(violationPath);
    };

    private final BiPredicate<String, String> checkSCMCache = (String projectKey, String date) -> {
        String folderPAth = CACHE_PATH + projectKey + "/" + date;
        Path scmPath = Paths.get(folderPAth + "/scm.json");
        logger.debug("Checking SCM cache json exist : projectKey: " + projectKey + " +date : " + date + " path : " + scmPath);
        return Files.exists(scmPath);
    };
    private final Function<String, StringBuilder> readCacheFile = folderPAth -> {
        List<String> data = null;
        try {
            data = (Files.readAllLines(Paths.get(folderPAth)));
        } catch (IOException e) {
            logger.error(e.getStackTrace());
        }
        StringBuilder analysisFileContent = new StringBuilder();
        Objects.requireNonNull(data).forEach(analysisFileContent::append);
        logger.info("Reading cache file : " + folderPAth);
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
        logger.debug("Convert date into milliseconds : " + date + " -> " + cachedTimestamp.getMillis());
        logger.debug("Convert API date  into milliseconds : " + apiDate + " -> " + apiTimestamp.getMillis());
        return timestamp;
    };

    private final BiFunction<String, String, Map<String, String>> getCachedData = (violation, scm) -> {
        Map<String, String> cachedFileData = new HashMap<>();
        cachedFileData.put("violation", violation);
        cachedFileData.put("scm", scm);
        logger.debug("Returning  violations and SCM  String data as Map");
        return cachedFileData;
    };
    @Value("${cache.enable}")
    private String cacheEnable;

    private void createCacheFile(String json, String projectKey, String date, String fileName) {
        String folderPAth = CACHE_PATH + projectKey + "/" + date;
        try {
            Path path = Paths.get(folderPAth);
            Files.createDirectories(path);
            Path filePath = Paths.get(folderPAth + "/" + fileName + ".json");
            logger.debug("Creating  cache file : " + folderPAth + "/" + fileName + ".json");
            Files.write(filePath, json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error(e.getStackTrace());
        }
    }

    public final BiFunction<String, String, Map<String, String>> checkCacheEnableViolation = (projectKey, date) -> {
        if (cacheEnable.equals("true")) {
            logger.debug("Cache enable for violation check : true");
            return checkAnalysisCache.apply(projectKey, date);
        } else if (cacheEnable.equals("false")) {
            logger.debug("Cache enable for violation check : false");
            List<Object> violationAPI = violationService.getViolation.apply(projectKey, date);
            JSONArray jsonArrViolation = jsonUtil.listToJsonArray.apply(violationAPI);
            //create cache file
            Map<String, String> cachedFileData = new HashMap<>();
            cachedFileData.put("violation", jsonArrViolation.toJSONString());

            return cachedFileData;
        }
        return null;
    };

    public final BiFunction<String, String, Map<String, String>> checkCacheEnableSCM = (projectKey, date) -> {
        if (cacheEnable.equals("true")) {
            logger.debug("Cache enable for SCM check : true");
            return checkAnalysisCache.apply(projectKey, date);
        } else if (cacheEnable.equals("false")) {
            logger.debug("Cache enable for SCM check : false");
            List<Object> scmAPI = scmService.getCommits.apply(projectKey, date);
            JSONArray jsonArrSCM = jsonUtil.listToJsonArray.apply(scmAPI);
            //create cache file
            Map<String, String> cachedFileData = new HashMap<>();
            cachedFileData.put("scm", jsonArrSCM.toJSONString());

            return cachedFileData;
        }
        return null;
    };

}