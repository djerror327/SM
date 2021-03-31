package com.dinusha.soft.service;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

@Service
public class SonarAuthHeaderService {
    private static final Logger logger = Logger.getLogger(SonarAuthHeaderService.class);
    @Value("${sonar.username}")
    private String sonarUsername;
    @Value("${sonar.ps}")
    private String sonarPs;

    Supplier<String> authHeader = () -> {
        String auth = sonarUsername + ":" + sonarPs;
        byte[] encodeAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        logger.debug("Generating Auth header");
        return ("Basic " + new String(encodeAuth));
    };
}
