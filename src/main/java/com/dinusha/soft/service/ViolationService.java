package com.dinusha.soft.service;

import com.dinusha.soft.utills.JsonUtil;
import com.dinusha.soft.utills.Paginate;
import com.dinusha.soft.webclient.Client;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.function.Function;

public interface ViolationService {
    Logger LOGGER = Logger.getLogger(ViolationService.class);


    //        SonarQubeOpenViolationMonitor
    Function<String, List<String>> GET_VIOLATION = url -> {

        String date = "2021-03";

        List<String> branches = BranchService.GET_BRANCHES.get();

        String violation = Client.GET.apply("localhost:9000/api/issues/search?projectKeys=SonarQubeOpenViolationMonitor&resolved=false&branch=dev&ps=3");
        JSONObject violationData = JsonUtil.JSON_OBJECT.apply(violation);

        //calculate paging count
        JSONObject paging = (JSONObject) violationData.get("paging");
        long recursionCount = Paginate.RECURSION_COUNT.applyAsLong(paging);

        //loop all pages and collect violation data
        //violation count for given YYYY-mm
        int violationCount = 0;
        for (int page = 1; page <= recursionCount; page++) {
            String violationObj = Client.GET.apply("localhost:9000/api/issues/search?projectKeys=SonarQubeOpenViolationMonitor&resolved=false&branch=dev&ps=3&p=" + page + "");
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
                System.out.println(updateMonth);


            }


            System.out.println();

        }
        System.out.println("Violation count :" + violationCount);
        return null;
    };

}
