package com.dinusha.soft.webclient;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

@Component
public class Client {
    Logger logger = Logger.getLogger(Client.class);
    public final UnaryOperator<String> get = url -> {
        WebClient webClient = WebClient.create();
        logger.info("GET -> " + url);
        return Objects.requireNonNull(webClient.get()
                .uri(url)
                .exchange()
                .block())
                .bodyToMono(String.class).retryWhen(Retry.fixedDelay(10, Duration.ofMillis(5000)))
                .block();
    };

    public final BinaryOperator<String> getWithAuthHeader = (authHeader, url) -> {
        WebClient webClient = WebClient.create();
        logger.info("GET -> " + url);
        return Objects.requireNonNull(webClient.get()
                .uri(url)
                .header("Authorization", authHeader)
                .exchange()
                .block())
                .bodyToMono(String.class).retryWhen(Retry.fixedDelay(10, Duration.ofMillis(5000)))
                .block();
    };

}
