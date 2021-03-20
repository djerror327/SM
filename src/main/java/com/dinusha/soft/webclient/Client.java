package com.dinusha.soft.webclient;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Objects;
import java.util.function.UnaryOperator;


public interface Client {
    UnaryOperator<String> GET = url -> {
        WebClient webClient = WebClient.create();
        return Objects.requireNonNull(webClient.get()
                .uri(url)
                .exchange()
                .block())
                .bodyToMono(String.class).retryWhen(Retry.fixedDelay(10, Duration.ofMillis(5000)))
                .block();
    };

}
