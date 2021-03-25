package com.dinusha.soft.service;

import com.dinusha.soft.utills.JsonUtil;
import com.dinusha.soft.utills.Paginate;
import com.dinusha.soft.webclient.Client;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public interface SonarProject {
    Supplier<List<String>> PROJECTS = () -> {

        //paging related part
        String pagingData = Client.GET_WITH_AUTH_HEADER.apply(SonarAuthHeader.AUTH_HEADER.get(), "http://localhost:9000/api/projects/search?ps=3&");
        JSONObject pageObj = JsonUtil.JSON_OBJECT.apply(pagingData);

        //calculate paging count
        JSONObject paging = (JSONObject) pageObj.get("paging");
        long recursionCount = Paginate.RECURSION_COUNT.applyAsLong(paging);

        ArrayList<String> projectKeys = new ArrayList<>();
        //loop all pages and collect violation data
        for (int page = 1; page <= recursionCount; page++) {
            String projects = Client.GET_WITH_AUTH_HEADER.apply(SonarAuthHeader.AUTH_HEADER.get(), "http://localhost:9000/api/projects/search?ps=3&p=" + page + "");
            JSONObject jsonProjects = JsonUtil.JSON_OBJECT.apply(projects);
            JSONArray issueArr = (JSONArray) jsonProjects.get("components");
            for (Object project : issueArr) {
                JSONObject projectObj = (JSONObject) project;
                String projectKey = projectObj.get("key").toString();
                projectKeys.add(projectKey);
            }
        }
        return projectKeys;
    };
}
