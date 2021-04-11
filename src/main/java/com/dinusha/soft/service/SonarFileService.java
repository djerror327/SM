package com.dinusha.soft.service;

import com.dinusha.soft.utills.JsonUtil;
import com.dinusha.soft.utills.Paginate;
import com.dinusha.soft.webclient.Client;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class SonarFileService {

    private static final Logger logger = Logger.getLogger(SonarFileService.class);

    @Autowired
    private SonarAuthHeaderService sonarAuthHeaderService;
    @Autowired
    private BranchService branchService;
    @Value("${sonar.host}")
    private String host;

    public final Function<String, Map<String, List<String>>> getFiles = projectKey -> {
        List<String> branchesList = branchService.getBranches.apply(projectKey);
        Map<String, List<String>> branchFiles = new HashMap<>();

        logger.info("Reading all files of SonarQube branch list");
        for (String branch : branchesList) {
            String pagingData = Client.GET_WITH_AUTH_HEADER.apply(sonarAuthHeaderService.authHeader.get(), host + "api/measures/component_tree?ps=500&component=" + projectKey + "&branch=" + branch + "&metricKeys=ncloc");

            JSONObject pageObj = JsonUtil.JSON_OBJECT.apply(pagingData);
            //calculate paging count
            JSONObject paging = (JSONObject) pageObj.get("paging");
            long recursionCount = Paginate.RECURSION_COUNT.applyAsLong(paging);

            //collect all file paths for particular branch
            List<String> files = new ArrayList<>();

            //loop all pages and collect violation data
            logger.debug("started to paginate");
            for (int page = 1; page <= recursionCount; page++) {
                String fileObj = Client.GET_WITH_AUTH_HEADER.apply(sonarAuthHeaderService.authHeader.get(), host + "api/measures/component_tree?ps=500&component=" + projectKey + "&branch=" + branch + "&metricKeys=ncloc&p=" + page + "");
                JSONObject jsonFiles = JsonUtil.JSON_OBJECT.apply(fileObj);
                JSONArray components = (JSONArray) jsonFiles.get("components");
                logger.debug("Reading components");
                for (Object component : components) {
                    JSONObject componentObj = (JSONObject) component;
                    String qualifier = componentObj.get("qualifier").toString();
                    // filter only files
                    if (qualifier.equals("FIL")) {
                        String filePath = componentObj.get("path").toString();
                        logger.debug("Branch : " + branch + " file : " + filePath);
                        files.add(filePath);
                    }
                }
            }
            branchFiles.put(branch, files);
        }
        logger.info("Reading all files of SonarQube branch list completed");
        return branchFiles;
    };
}
