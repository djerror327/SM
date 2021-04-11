package com.dinusha.soft.webclient;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Objects;
import java.util.function.BinaryOperator;

@Configuration
public class Client {
    private static final Logger logger = Logger.getLogger(Client.class);
    public final BinaryOperator<String> getWithAuthHeader = (authHeader, uri) -> {
        logger.info("GET -> " + uri);
        String result = null;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(uri);
        request.setHeader("Authorization", authHeader);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = Objects.requireNonNull(response).getEntity();
        try {
            result = EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    };
    @Value("${web.client.retry.count}")
    private String retryCount;


    //    public final UnaryOperator<String> get = url -> {
////        WebClient webClient = WebClient.create();
//        logger.info("GET -> " + url);
//        return Objects.requireNonNull(
//                webClientBuilder()
//                        .build()
//                        .get()
//                        .uri(url)
//                        .exchange().block())
//                .bodyToMono(String.class)
//                .retryWhen(Retry.fixedDelay(Integer.parseInt(retryCount), Duration.ofMillis(Integer.parseInt(retryDelay))))
//                .block()
//                ;
//    };
    @Value("${web.client.retry.delay}")
    private String retryDelay;
}
