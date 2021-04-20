package com.dinusha.soft.webclient;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Objects;
import java.util.function.BinaryOperator;

@Configuration
public class Client {
    private static final Logger logger = Logger.getLogger(Client.class);
    public final BinaryOperator<String> getWithAuthHeader = (authHeader, uri) -> {

        logger.info("GET -> " + uri);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setHeader("Authorization", authHeader);
            try (CloseableHttpResponse response = client.execute(httpGet)) {

                HttpEntity entity = Objects.requireNonNull(response).getEntity();
                if (response.getStatusLine().getStatusCode() == 200) {
                    logger.info("Status Code : " + response.getStatusLine().getStatusCode());
                } else {
                    logger.warn("Status Code : " + response.getStatusLine().getStatusCode());
                }
                return EntityUtils.toString(entity);
            }
        } catch (IOException e) {
            logger.error(e.getStackTrace());
        }
        return null;
    };
}
