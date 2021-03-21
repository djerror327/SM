package com.dinusha.soft.service;

import com.dinusha.soft.utills.JsonUtil;
import com.dinusha.soft.webclient.Client;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public interface BranchService {
    Logger LOGGER = Logger.getLogger(BranchService.class);
    @SuppressWarnings("unchecked")
    Supplier<List<String>> GET_BRANCHES = () -> {
        LOGGER.debug("Retrieving branch data from API");
        JSONObject branches = JsonUtil.JSON_OBJECT.apply(Client.GET.apply("http://localhost:9000/api/project_branches/list?project=SonarQubeOpenViolationMonitor"));
        JSONArray branchList = (JSONArray) branches.get("branches");
        List<String> list = new ArrayList<>();
        branchList.forEach(payload -> list.add(((JSONObject) payload).get("name").toString()));
        LOGGER.debug("Returning Sonar branch list");
        return list;
    };
}