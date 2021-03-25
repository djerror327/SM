package com.dinusha.soft.service;

import com.dinusha.soft.utills.JsonUtil;
import com.dinusha.soft.webclient.Client;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@PropertySource("classpath:application.properties")
public class BranchService {

    private static final Logger logger = Logger.getLogger(BranchService.class);
    @SuppressWarnings("unchecked")
    public static final Function<String, List<String>> getBranches = key -> {

        logger.debug("Retrieving branch data from API");
        JSONObject branches = JsonUtil.JSON_OBJECT.apply(Client.GET.apply("http://localhost:9000/"
                + "api/project_branches/list?project=" + key));
        JSONArray branchList = (JSONArray) branches.get("branches");
        List<String> list = new ArrayList<>();
        branchList.forEach(payload -> list.add(((JSONObject) payload).get("name").toString()));
        logger.debug("Returning Sonar branch list");
        return list;
    };
    @Value("${server.port}")
    private String host;
}