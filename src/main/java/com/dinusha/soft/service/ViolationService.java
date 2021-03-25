package com.dinusha.soft.service;

import com.dinusha.soft.utills.JsonUtil;
import com.dinusha.soft.utills.Paginate;
import com.dinusha.soft.webclient.Client;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public interface ViolationService {
    Logger LOGGER = Logger.getLogger(ViolationService.class);

    //SonarQubeOpenViolationMonitor
    Function<String, Map<String, Integer>> GET_VIOLATION = url -> {

        //given date
        String date = "2021-03";

        Map<String, Integer> result = new HashMap<>();

        List<String> branches = BranchService.getBranches.apply(null);

        //violation count for given YYYY-mm
        int violationCount = 0;
        for (String branch : branches) {

            //initialize violation count to 0 for each branch
            violationCount = 0;
            //paging related part
            String pagingData = Client.GET.apply("localhost:9000/api/issues/search?projectKeys=SonarQubeOpenViolationMonitor&resolved=false&branch=" + branch + "&ps=3");
            JSONObject pageObj = JsonUtil.JSON_OBJECT.apply(pagingData);

            //calculate paging count
            JSONObject paging = (JSONObject) pageObj.get("paging");
            long recursionCount = Paginate.RECURSION_COUNT.applyAsLong(paging);

            //loop all pages and collect violation data
            for (int page = 1; page <= recursionCount; page++) {
                String violationObj = Client.GET.apply("localhost:9000/api/issues/search?projectKeys=SonarQubeOpenViolationMonitor&resolved=false&branch=" + branch + "&ps=3&p=" + page + "");
                JSONObject jsonViolation = JsonUtil.JSON_OBJECT.apply(violationObj);
                JSONArray issueArr = (JSONArray) jsonViolation.get("issues");
                for (Object issue : issueArr) {
                    JSONObject issueObj = (JSONObject) issue;
                    String updateDate = issueObj.get("updateDate").toString();
                    String updateMonth = updateDate.substring(0, 7);

                    //calculate violations for given month
                    if (updateMonth.equals(date)) {
                        violationCount += 1;
                    }
                }
            }
            System.out.println("Violation count :" + violationCount);
            result.put(branch, violationCount);
        }
        return result;
    };
}
