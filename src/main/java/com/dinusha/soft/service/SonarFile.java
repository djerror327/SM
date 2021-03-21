package com.dinusha.soft.service;

import com.dinusha.soft.utills.JsonUtil;
import com.dinusha.soft.utills.Paginate;
import com.dinusha.soft.webclient.Client;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public interface SonarFile {

    BiFunction<String, String, Map<String, List<String>>> GET_FILES = (url, projectKey) -> {
        List<String> branches = BranchService.GET_BRANCHES.get();
        Map<String, List<String>> branchFiles = new HashMap<>();
        for (String branch : branches) {
            String pagingData = Client.GET.apply("http://localhost:9000/api/measures/component_tree?ps=3&component=SonarQubeOpenViolationMonitor&branch=" + branch + "&metricKeys=ncloc");

            JSONObject pageObj = JsonUtil.JSON_OBJECT.apply(pagingData);
            //calculate paging count
            JSONObject paging = (JSONObject) pageObj.get("paging");
            long recursionCount = Paginate.RECURSION_COUNT.applyAsLong(paging);

            //collect all file path for particular branch
            List<String> files = new ArrayList<>();

            //loop all pages and collect violation data
            for (int page = 1; page <= recursionCount; page++) {
                String fileObj = Client.GET.apply("http://localhost:9000/api/measures/component_tree?ps=3&component=SonarQubeOpenViolationMonitor&branch=" + branch + "&metricKeys=ncloc&p=" + page + "");
                JSONObject jsonFiles = JsonUtil.JSON_OBJECT.apply(fileObj);
                JSONArray components = (JSONArray) jsonFiles.get("components");
                for (Object component : components) {
                    JSONObject componentObj = (JSONObject) component;
                    String qualifier = componentObj.get("qualifier").toString();

                    //calculate violations for given month
                    /** todo externalize qualifier*/
                    if (qualifier.equals("FIL")) {
                        String filePath = componentObj.get("path").toString();
                        files.add(filePath);
                    }
                }
            }
//            System.out.println(files);
            branchFiles.put(branch, files);
        }
        return branchFiles;
    };
}
