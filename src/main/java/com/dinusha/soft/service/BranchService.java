package com.dinusha.soft.service;

import com.dinusha.soft.utills.JsonUtil;
import com.dinusha.soft.utills.SonarAuthHeaderService;
import com.dinusha.soft.webclient.Client;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class BranchService {

    private final Logger logger = Logger.getLogger(BranchService.class);

    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private Client client;
    @Autowired
    private SonarAuthHeaderService sonarAuthHeaderService;
    @Value("${sonar.host}")
    private String host;

    //return all branches of specific SonarQube project
    @SuppressWarnings("unchecked")
    public final Function<String, List<String>> getBranches = key -> {

        logger.debug("Retrieving branch data from API");
        JSONObject branches = jsonUtil.stringToJsonObject.apply(client.getWithAuthHeader.apply(sonarAuthHeaderService.authHeader.get(), host + "api/project_branches/list?project=" + key));
        JSONArray branchList = (JSONArray) branches.get("branches");
        List<String> list = new ArrayList<>();
        branchList.forEach(payload -> list.add(((JSONObject) payload).get("name").toString()));
        logger.debug("Returning Sonar branch list");
        return list;
    };

}