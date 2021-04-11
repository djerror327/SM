package com.dinusha.soft.utills;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class JsonUtil {
    private static final Logger logger = Logger.getLogger(JsonUtil.class);
    public final Function<String, JSONObject> jsonObject = value -> {
        logger.debug("Passing to JSON Object");
        JSONParser parser = new JSONParser();
        JSONObject object = null;
        try {
            object = (JSONObject) parser.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return object;
    };

    public final Function<String, JSONArray> jsonArray = value -> {
        logger.debug("Passing to JSON Array");
        JSONParser parser = new JSONParser();
        JSONArray jsonArr = null;
        try {
            jsonArr = (JSONArray) parser.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jsonArr;
    };
}
