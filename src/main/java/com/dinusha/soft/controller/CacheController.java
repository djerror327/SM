package com.dinusha.soft.controller;

import com.dinusha.soft.cache.CommonCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheController {

    @Autowired
    private CommonCache commonCache;

    @GetMapping("/v1/cache/evict")
    public String deleteCache() {
        if (commonCache.deleteAllCache.getAsBoolean()) {
            return "Cache successfully cleared!";
        } else {
            return "Cache folder does not exist to clear!";
        }
    }
}
