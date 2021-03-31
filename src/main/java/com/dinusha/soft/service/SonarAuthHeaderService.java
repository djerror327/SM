package com.dinusha.soft.service;


import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

@Service
public class SonarAuthHeaderService {

    Supplier<String> AUTH_HEADER = () -> {
//        String username = env.getProperty("sonar.username");
//        String ps = env.getProperty("sonar.ps");

        String auth = "admin" + ":" + "admin";
        byte[] encodeAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        return ("Basic " + new String(encodeAuth));
    };

}
