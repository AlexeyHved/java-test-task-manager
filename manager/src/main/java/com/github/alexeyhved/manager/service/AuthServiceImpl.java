package com.github.alexeyhved.manager.service;

import com.github.alexeyhved.manager.exception.ErrorResponse;
import com.github.alexeyhved.manager.exception.ExceptionWrapper;
import com.github.alexeyhved.manager.dto.JwtRequest;
import com.github.alexeyhved.manager.dto.JwtResponse;
import com.github.alexeyhved.manager.dto.RefreshJwtRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final WebClient webClient;
    private final Scheduler blockingPool;

    public Mono<JwtResponse> registration(JwtRequest request) {
        return Mono.from(
                webClient.post()
                        .uri("/auth/register")
                        .header(MediaType.APPLICATION_JSON_VALUE)
                        .body(BodyInserters.fromValue(request))
                        .exchangeToMono(this::handleMonoResponse)
                        .log()
        ).subscribeOn(blockingPool);
    }

    public Mono<JwtResponse> login(JwtRequest request) {
        return Mono.from(
                webClient.post()
                        .uri("/auth/login")
                        .header(MediaType.APPLICATION_JSON_VALUE)
                        .body(BodyInserters.fromValue(request))
                        .exchangeToMono(this::handleMonoResponse)
                        .log()
        ).subscribeOn(blockingPool);
    }

    public Mono<JwtResponse> updateToken(RefreshJwtRequest request) {
        return Mono.from(
                webClient.post()
                .uri("/auth/token")
                .header(MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(request))
                .exchangeToMono(this::handleMonoResponse)
                .log()
        ).subscribeOn(blockingPool);
    }

    public Mono<Void> delete(String bearer) {
        return Mono.from(
                webClient.delete()
                .uri("/auth/delete")
                .header("Authorization", bearer)
                .exchangeToMono(this::handleVoidResponse)
                .log()
        ).subscribeOn(blockingPool);
    }

    private Mono<Void> handleVoidResponse(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isError()) {
            return clientResponse.bodyToMono(ErrorResponse.class)
                    .flatMap(errorResponse -> Mono.error(new ExceptionWrapper(errorResponse)));
        }
        return clientResponse.releaseBody();
    }

    private Mono<JwtResponse> handleMonoResponse(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isError()) {
            return clientResponse.bodyToMono(ErrorResponse.class)
                    .flatMap(errorResponse -> Mono.error(new ExceptionWrapper(errorResponse)));
        }
        return clientResponse.bodyToMono(JwtResponse.class);
    }
}
