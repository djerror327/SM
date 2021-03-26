package com.dinusha.soft.utills;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.util.function.ToLongFunction;

@Component
public class Paginate {

    public ToLongFunction<JSONObject> RECURSION_COUNT = paging -> {
        double total = (long) paging.get("total");
        double pageSize = (long) paging.get("pageSize");
        return (long) Math.ceil(total / pageSize);
    };
}
