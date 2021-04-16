package com.dinusha.soft.utills;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class JsonUtil {
    private static final Logger logger = Logger.getLogger(JsonUtil.class);
    public final Function<String, JSONObject> stringToJsonObject = value -> {
        logger.debug("Passing to JSON Object");
        JSONParser parser = new JSONParser();
        JSONObject object = null;
        try {
            object = (JSONObject) parser.parse(value);
        } catch (ParseException e) {
            logger.error(e.getStackTrace());
        }
        return object;
    };

    public final Function<String, JSONArray> stringToJsonArray = value -> {
        logger.debug("Passing to JSON Array");
        JSONParser parser = new JSONParser();
        JSONArray jsonArr = null;
        try {
            jsonArr = (JSONArray) parser.parse(value);
        } catch (ParseException e) {
            logger.error(e.getStackTrace());
        }
        return jsonArr;
    };

    public final Function<List<Object>, String> listToJsonStringArray = value -> {
        logger.debug("Passing List to Json String Array");
        return JSONArray.toJSONString(value);
    };

    public final Function<List<Object>, JSONArray> listToJsonArray = value -> {
        logger.debug("Passing List to Json Array");
        return stringToJsonArray.apply(JSONArray.toJSONString(value));
    };

    @SuppressWarnings("rawtypes")
    public final Function<Map, JSONObject> mapToJsonObject = value -> {
        logger.debug("Passing Map to Json Object");
        return new JSONObject(value);
    };
}
