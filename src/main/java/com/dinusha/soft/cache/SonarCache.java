package com.dinusha.soft.cache;

import com.dinusha.soft.service.SCMService;
import com.dinusha.soft.service.ViolationService;
import com.dinusha.soft.utills.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class SonarCache {

    @Autowired
    private ViolationService violationService;
    @Autowired
    private SCMService scmService;
    @Autowired
    private JsonUtil jsonUtil;

    public String createViolationCache(String projectKey, String date) {
        //get json from api
        List<Object> violationList = violationService.getViolation.apply(projectKey, date);
        String jsonViolationArray = jsonUtil.listToJsonStringArray.apply(violationList);

        //save it into json file in the cache folder
        System.out.println(jsonViolationArray);
        createCacheFile(jsonViolationArray, projectKey, date, "violation");
        return jsonViolationArray;
    }

    public void createSCMCache(String projectKey, String date) {
        //get json from api
        List<Object> scmList = scmService.getCommits.apply(projectKey, date);
        String jsonSCMnArray = jsonUtil.listToJsonStringArray.apply(scmList);

        //save it into json file in the cache folder
        createCacheFile(jsonSCMnArray, projectKey, date, "scm");
        System.out.println(jsonSCMnArray);
    }

    public void createCacheFile(String json, String projectKey, String date, String fileName) {
        String folderPAth = "./cache/" + projectKey + "/" + date;

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
