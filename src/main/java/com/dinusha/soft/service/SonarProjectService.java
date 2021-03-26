package com.dinusha.soft.service;

import com.dinusha.soft.utills.JsonUtil;
import com.dinusha.soft.utills.Paginate;
import com.dinusha.soft.webclient.Client;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class SonarProjectService {

    private static final Logger logger = Logger.getLogger(SonarProjectService.class);
    @Value("${sonar.host}")
    private String host;

    public Supplier<List<String>> PROJECTS = () -> {
        //paging related part
        logger.debug("Reading paging sizes");
        String pagingData = new Client().GET_WITH_AUTH_HEADER.apply(new SonarAuthHeader().AUTH_HEADER.get(), host + "api/projects/search?ps=500&");
        JSONObject pageObj = new JsonUtil().JSON_OBJECT.apply(pagingData);

        //calculate paging count
        JSONObject paging = (JSONObject) pageObj.get("paging");
        long recursionCount = new Paginate().RECURSION_COUNT.applyAsLong(paging);

        ArrayList<String> projectKeys = new ArrayList<>();
        //loop all pages and collect violation data
        logger.info("Reading project list from SonarQube");
        for (int page = 1; page <= recursionCount; page++) {
            String projects = new Client().GET_WITH_AUTH_HEADER.apply(new SonarAuthHeader().AUTH_HEADER.get(), host + "api/projects/search?ps=500&p=" + page + "");
            JSONObject jsonProjects = new JsonUtil().JSON_OBJECT.apply(projects);
            JSONArray issueArr = (JSONArray) jsonProjects.get("components");
            for (Object project : issueArr) {
                JSONObject projectObj = (JSONObject) project;
                String projectKey = projectObj.get("key").toString();
                projectKeys.add(projectKey);
            }
        }
        logger.debug("Reading project list from SonarQube is completed!");
        return projectKeys;
    };
}
