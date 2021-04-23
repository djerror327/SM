package com.dinusha.soft.cache;

import com.dinusha.soft.service.AnalysisService;
import com.dinusha.soft.utills.JsonUtil;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

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
public class CommonCache {

    private static final Logger logger = Logger.getLogger(CommonCache.class);

    private static final String CACHE_PATH = "./cache/";

    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private AnalysisService analysisService;

    Supplier<String> getCachedPath = () -> CACHE_PATH;

    public final BooleanSupplier deleteAllCache = () -> {
        try {
            logger.info("Deleting cache folder");
            return FileSystemUtils.deleteRecursively(Paths.get(CACHE_PATH));
        } catch (IOException e) {
            e.getStackTrace();
            logger.error(e.getStackTrace());
        }
        logger.warn("Cache folder is not deleted!");
        return false;
    };

    final Consumer<String> createBranchAnalysisCacheJson = projectKey -> {
        Map<String, Map<String, String>> branchesAnalysis = analysisService.getBranchesAnalysis(projectKey);
        JSONObject jsonObject = jsonUtil.mapToJsonObject.apply(branchesAnalysis);
        logger.debug("Creating Branch Analysis cache data : project key : " + projectKey);

        String folderPAth = CACHE_PATH + projectKey;
        try {
            Path path = Paths.get(folderPAth);
            Files.createDirectories(path);
            Path filePath = Paths.get(folderPAth + "/" + "branchAnalysis.json");
            logger.info("Creating branchAnalysis.json : project key : " + projectKey);
            Files.write(filePath, jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error(e.getStackTrace());
        }
    };

    final BooleanSupplier checkCacheFolderExist = () -> {
        logger.debug("Checking cache folder exist");
        return Files.isDirectory(Paths.get(CACHE_PATH));
    };

    final Predicate<String> checkCacheFile = filePath -> {
        logger.debug("Checking cache file path exist : " + filePath);
        return Files.exists(Paths.get(filePath));
    };

    final Function<String, StringBuilder> readCacheFile = folderPAth -> {
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

    final BiFunction<String, String, Map<String, Long>> getMillis = (date, apiDate) -> {
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

    void createCacheFile(String json, String projectKey, String date, String fileName) {
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
}