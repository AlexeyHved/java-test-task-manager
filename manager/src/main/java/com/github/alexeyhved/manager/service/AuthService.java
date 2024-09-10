package com.github.alexeyhved.manager.service;

import com.github.alexeyhved.manager.dto.JwtRequest;
import com.github.alexeyhved.manager.dto.JwtResponse;
import com.github.alexeyhved.manager.dto.RefreshJwtRequest;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<JwtResponse> registration(JwtRequest request);

    Mono<JwtResponse> login(JwtRequest request);

    Mono<JwtResponse> updateToken(RefreshJwtRequest request);

    Mono<Void> delete(String bearer);
}
