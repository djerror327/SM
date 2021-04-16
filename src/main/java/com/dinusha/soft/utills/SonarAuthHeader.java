package com.dinusha.soft.utills;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

@Configuration
public class SonarAuthHeader {
    private static final Logger logger = Logger.getLogger(SonarAuthHeader.class);
    @Value("${sonar.username}")
    private String sonarUsername;
    @Value("${sonar.ps}")
    private String sonarPs;

    public final Supplier<String> authHeader = this::authHeaderBean;

    @Bean
    public String authHeaderBean() {
        String auth = sonarUsername + ":" + sonarPs;
        byte[] encodeAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        logger.debug("Generating Auth header");
        return ("Basic " + new String(encodeAuth));
    }
}
