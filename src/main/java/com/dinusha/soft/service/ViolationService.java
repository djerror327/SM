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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Service
public class ViolationService {
    private static final Logger logger = Logger.getLogger(ViolationService.class);

    @Autowired
    private BranchService branchService;
    @Value("${sonar.host}")
    private String host;

    //SonarQubeOpenViolationMonitor
    public final BiFunction<String, String, Map<String, Integer>> getViolation = (sonarProjectKey, date) -> {

        Map<String, Integer> result = new HashMap<>();
        List<String> branchesList = branchService.getBranches.apply(sonarProjectKey);

        //violation count for given YYYY-mm (filter for specific month)
        int violationCount = 0;
        logger.debug("Reading violations of all branches of SonarQube project key : " + sonarProjectKey);
        for (String branch : branchesList) {

            //initialize violation count to 0 for each branch
            violationCount = 0;
            //paging related part
            logger.debug("Reading paging sizes");
            String pagingData = Client.GET.apply(host + "api/issues/search?projectKeys=" + sonarProjectKey + "&resolved=false&branch=" + branch + "&ps=500");
            JSONObject pageObj = JsonUtil.JSON_OBJECT.apply(pagingData);

            //calculate paging count
            JSONObject paging = (JSONObject) pageObj.get("paging");
            long recursionCount = Paginate.RECURSION_COUNT.applyAsLong(paging);

            //loop all pages and collect violation data
            logger.info("Reading violations of branch : " + branch);
            for (int page = 1; page <= recursionCount; page++) {
                String violationObj = Client.GET.apply(host + "api/issues/search?projectKeys=" + sonarProjectKey + "&resolved=false&branch=" + branch + "&ps=500&p=" + page + "");
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
            logger.info("Reading violations of branch completed : " + branch);
            result.put(branch, violationCount);
        }
        logger.debug("Reading violations of all branches of SonarQube project key : " + sonarProjectKey + " completed");
        return result;
    };
}
