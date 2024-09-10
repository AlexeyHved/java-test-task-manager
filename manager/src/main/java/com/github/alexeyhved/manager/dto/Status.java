package com.github.alexeyhved.manager.dto;

import reactor.core.publisher.Mono;

import java.util.Arrays;

public enum Status {
    WAITING,
    PROCESSING,
    COMPLETED;

    public static Mono<Status> mapTo(String value) {
        boolean anyMatch = Arrays.stream(Status.values())
                .map(Status::name)
                .anyMatch(name -> name.equals(value));

        if (anyMatch) {
            return Mono.just(valueOf(value));
        }
        return Mono.error(new IllegalArgumentException());
    }
}
