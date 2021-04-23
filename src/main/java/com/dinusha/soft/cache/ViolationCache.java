package com.dinusha.soft.cache;

import com.dinusha.soft.service.AnalysisService;
import com.dinusha.soft.service.ViolationService;
import com.dinusha.soft.utills.JsonUtil;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;

@Component
public class ViolationCache {

    private static final Logger logger = Logger.getLogger(ViolationCache.class);

    @Autowired
    private ViolationService violationService;
    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private AnalysisService analysisService;
    @Autowired
    private CommonCache commonCache;
    @Value("${cache.enable}")
    private String cacheEnable;



    private final BinaryOperator<String> createViolationCache = (projectKey, date) -> {
        //get json from api
        List<Object> violationList = violationService.getViolation.apply(projectKey, date);
        String jsonViolationArray = jsonUtil.listToJsonStringArray.apply(violationList);

        //save it into json file in the cache folder
        logger.info("Creating violation cache : project key : " + projectKey + " : date : " + date);
        logger.debug("Writing into violation cache : " + jsonViolationArray);
        commonCache.createCacheFile(jsonViolationArray, projectKey, date, "violation");
        return jsonViolationArray;
    };

    private final BiPredicate<String, String> checkViolationCache = (String projectKey, String date) -> {
        String folderPAth = commonCache.getCachedPath.get() + projectKey + "/" + date;
        Path violationPath = Paths.get(folderPAth + "/violation.json");
        logger.debug("Checking violation cache json exist : projectKey: " + projectKey + " +date : " + date + " path : " + violationPath);
        return Files.exists(violationPath);
    };

    private final Function<String, Map<String, String>> getCachedData = violation -> {
        Map<String, String> cachedFileData = new HashMap<>();
        cachedFileData.put("violation", violation);
        logger.debug("Violations reading completed!");
        return cachedFileData;
    };

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
        String folderPAth = commonCache.getCachedPath.get() + projectKey + "/branchAnalysis.json";
        String violationPath = commonCache.getCachedPath.get() + projectKey + "/" + date + "/" + "violation.json";

        if (commonCache.checkCacheFolderExist.getAsBoolean() && commonCache.checkCacheFile.test(folderPAth)) {
            StringBuilder analysisFileContent = commonCache.readCacheFile.apply(folderPAth);
            JSONObject cachedJsonProject = jsonUtil.stringToJsonObject.apply(analysisFileContent.toString());
            for (Object key : cachedJsonProject.keySet()) {
                //if month is less than the api latest analysis month the return the cache if available. else get data from api and create a cached and return the cache file
                for (Object apiKey : jsonBranchesAPI.keySet()) {
                    //get api date
                    String apiDate = (jsonBranchesAPI.get(apiKey).toString()).substring(0, 10);
                    logger.debug("Removing API last analysis time and read only date");
                    //convert to 1st of the current date od api for ui match
                    Map<String, Long> timestamp = commonCache.getMillis.apply(date, apiDate);
                    //check cache folder is exist. if not create cache
                    //if date is smaller than api then return cache if not exist then create cache
                    logger.debug("Checking date milliseconds are less than API milliseconds");
                    if (timestamp.get("dateMillis") < timestamp.get("apiMillis")) {
                        //check cache available the return else create cache and return
                        if (checkViolationCache.test(projectKey, date)) {
                            return getCachedData.apply(String.valueOf(commonCache.readCacheFile.apply(violationPath)));
                        } else {
                            //create branch analysis cache json
                            commonCache.createBranchAnalysisCacheJson.accept(projectKey);
                            String violation = createViolationCache.apply(projectKey, date);
                            return getCachedData.apply(violation);
                        }
                    } else if (timestamp.get("dateMillis").equals(timestamp.get("apiMillis"))) {
                        logger.debug("Checking date milliseconds and API milliseconds are equal");
                        //if reading for current month
                        JSONObject cachedJsonBranches = (JSONObject) cachedJsonProject.get(key);
                        if (cachedJsonBranches.size() != branchesAPISize) {
                            logger.debug("Checking cached json branch and API branches sizes are different");
                            //create cache
                            //create branch analysis cache json
                            commonCache.createBranchAnalysisCacheJson.accept(projectKey);
                            String violation = createViolationCache.apply(projectKey, date);
                            //create branch analysis cache json
                            return getCachedData.apply(violation);
                        } else {
                            //check api branch analysis and cached jason file timestamp. if there is time mismatch then recreate cache
                            for (Object cachedKey : cachedJsonBranches.keySet()) {
                                String cachedDate = (cachedJsonBranches.get(cachedKey).toString()).substring(0, 10);
                                String apiDateCurrentMonth = (jsonBranchesAPI.get(cachedKey).toString()).substring(0, 10);
                                Map<String, Long> millis = commonCache.getMillis.apply(cachedDate, apiDateCurrentMonth);
                                if (millis.get("dateMillis") < (millis.get("apiMillis"))) {
                                    logger.debug("Checking date milliseconds are less than API milliseconds for current month");
                                    //create branch analysis cache json
                                    commonCache.createBranchAnalysisCacheJson.accept(projectKey);
                                    String violation = createViolationCache.apply(projectKey, date);
                                    //create branch analysis cache json
                                    return getCachedData.apply(violation);
                                }
                            }
                            if (checkViolationCache.test(projectKey, date)) {
                                logger.debug("Checking cache data is available for current month. return cached data");
                                return getCachedData.apply(String.valueOf(commonCache.readCacheFile.apply(violationPath)));
                            }
                            //if cache file not available for current month
                            else {
                                //create branch analysis cache json
                                logger.debug("No cache data is available for current month. creating cache for current month");
                                commonCache.createBranchAnalysisCacheJson.accept(projectKey);
                                String violation = createViolationCache.apply(projectKey, date);
                                //create branch analysis cache json
                                return getCachedData.apply(violation);
                            }
                        }
                    }
                    //ui timestamp is greater than api timestamp
                    else {
                        logger.debug("Scanning violations for future date. Directly reading from API");
                        List<Object> violationAPI = violationService.getViolation.apply(projectKey, date);
                        JSONArray jsonArrViolation = jsonUtil.listToJsonArray.apply(violationAPI);
                        //create cache file
                        return getCachedData.apply(jsonArrViolation.toJSONString());
                    }
                }
            }
        } else {
            logger.debug("Cache folder is not available.");
            //create branch analysis cache json
            commonCache.createBranchAnalysisCacheJson.accept(projectKey);
            //if cache folder is not available re create cache
            String violation = createViolationCache.apply(projectKey, date);
            return getCachedData.apply(violation);
        }
        return null;
    };

    public final BiFunction<String, String, Map<String, String>> checkCacheEnableViolation = (projectKey, date) -> {
        if (cacheEnable.equals("true")) {
            logger.debug("Cache enable for violation check : true");
            return checkAnalysisCache.apply(projectKey, date);
        } else if (cacheEnable.equals("false")) {
            logger.debug("Cache enable for violation check : false");
            List<Object> violationAPI = violationService.getViolation.apply(projectKey, date);
            JSONArray jsonArrViolation = jsonUtil.listToJsonArray.apply(violationAPI);
            return getCachedData.apply(jsonArrViolation.toJSONString());
        }
        return null;
    };
}