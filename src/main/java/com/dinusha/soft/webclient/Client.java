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
public interface Client {
    Logger LOGGER = Logger.getLogger(Client.class);
    UnaryOperator<String> GET = url -> {
        WebClient webClient = WebClient.create();
        LOGGER.info("GET -> " + url);
        return Objects.requireNonNull(webClient.get()
                .uri(url)
                .exchange()
                .block())
                .bodyToMono(String.class).retryWhen(Retry.fixedDelay(10, Duration.ofMillis(5000)))
                .block();
    };

    BinaryOperator<String> GET_WITH_AUTH_HEADER = (authHeader, url) -> {
        WebClient webClient = WebClient.create();
        LOGGER.info("GET -> " + url);
        return Objects.requireNonNull(webClient.get()
                .uri(url)
                .header("Authorization", authHeader)
                .exchange()
                .block())
                .bodyToMono(String.class).retryWhen(Retry.fixedDelay(10, Duration.ofMillis(5000)))
                .block();
    };

}
