package com.dinusha.soft.service;

import com.dinusha.soft.utills.JsonUtil;
import com.dinusha.soft.utills.Paginate;
import com.dinusha.soft.utills.SonarAuthHeader;
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
import java.util.function.BiFunction;

@Service
public class ViolationService {
    private static final Logger logger = Logger.getLogger(ViolationService.class);

    @Autowired
    private Paginate paginate;
    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private Client client;
    @Autowired
    private SonarAuthHeader sonarAuthHeaderService;
    @Autowired
    private BranchService branchService;
    @Value("${sonar.host}")
    private String host;
    @Value("${sonar.issue.limitation}")
    private String issueLimitation;

    //SonarQubeOpenViolationMonitor
    public final BiFunction<String, String, List<Object>> getViolation = (sonarProjectKey, date) -> {

        HashMap<String, Integer> result = new HashMap<>();
        List<String> branchesList = branchService.getBranches.apply(sonarProjectKey);

        //violation count for given YYYY-mm (filter for specific month)
        int violationCount;

        //violation for authors for specific branch section
        HashMap<String, Integer> authorsMap = new HashMap<>();
        HashMap<String, HashMap<String, Integer>> authorsBranch = new HashMap<>();

        logger.debug("Reading violations of all branches of SonarQube project key : " + sonarProjectKey);
        for (String branch : branchesList) {

            //initialize violation count to 0 for each branch
            violationCount = 0;
            //paging related part
            logger.debug("Reading paging sizes");
            String pagingData = client.getWithAuthHeader.apply(sonarAuthHeaderService.authHeader.get(), host + "api/issues/search?projectKeys=" + sonarProjectKey + "&resolved=false&branch=" + branch + "&ps=500");
            JSONObject pageObj = jsonUtil.stringToJsonObject.apply(pagingData);

            //calculate paging count
            JSONObject paging = (JSONObject) pageObj.get("paging");
            long recursionCount = paginate.recursionCount.applyAsLong(paging);

            //loop all pages and collect violation data
            logger.info("Reading violations of branch : " + branch);
            for (int page = 1; page <= recursionCount; page++) {

                //community edition api limitation logic
                int limitation = Integer.parseInt(issueLimitation);
                if (limitation <= page * 500) {
                    logger.debug("API limitation reach limit : " + limitation + " -> " + page * 500);
                    break;
                }

                String violationObj = client.getWithAuthHeader.apply(sonarAuthHeaderService.authHeader.get(), host + "api/issues/search?projectKeys=" + sonarProjectKey + "&resolved=false&branch=" + branch + "&ps=500&p=" + page + "");
                JSONObject jsonViolation = jsonUtil.stringToJsonObject.apply(violationObj);
                JSONArray issueArr = (JSONArray) jsonViolation.get("issues");
                for (Object issue : issueArr) {
                    JSONObject issueObj = (JSONObject) issue;
                    //get updated date
                    String updateDate = issueObj.get("updateDate").toString();
                    //get author
                    String author = issueObj.get("author").toString();
                    String updateMonth = updateDate.substring(0, 7);

                    //calculate violations for given month
                    if (updateMonth.equals(date)) {
                        if (authorsMap.containsKey(author)) {
                            //check author is available then increment violations one by one
                            int authorViolations = authorsMap.get(author);
                            authorsMap.put(author, ++authorViolations);
                        } else {
                            //if author is new add 1 violation initially
                            authorsMap.put(author, 1);
                        }
                        violationCount += 1;
                    }
                }
            }
            logger.info("Reading violations of branch completed : " + branch);
            result.put(branch, violationCount);
            //set authors for branches with violation counts
            authorsBranch.put(branch, authorsMap);
            //set authors Map to 0 after analyzing specific branch
            authorsMap = new HashMap<>();
        }
        logger.debug("Reading violations of all branches of SonarQube project key : " + sonarProjectKey + " completed");

        //creating final output
        HashMap<String, HashMap<String, Integer>> branches = new HashMap<>();
        branches.put("branch", result);
        HashMap<String, HashMap<String, HashMap<String, Integer>>> users = new HashMap<>();
        users.put("user", authorsBranch);
        List<Object> output = new ArrayList<>();
        output.add(branches);
        output.add(users);

        return output;
    };
}
