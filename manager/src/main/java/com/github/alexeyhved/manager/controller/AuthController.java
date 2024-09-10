package com.github.alexeyhved.manager.controller;

import com.github.alexeyhved.manager.dto.*;
import com.github.alexeyhved.manager.service.AuthService;
import com.github.alexeyhved.manager.service.JwtProvider;
import com.github.alexeyhved.manager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.github.alexeyhved.manager.util.Const.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Validated
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Operation(summary = "New user registration")
    @PostMapping( value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<JwtResponse>> registration(@Valid @RequestBody JwtRequest request) {
        return authService.registration(request)
                .flatMap(jwtResponse -> jwtProvider
                        .validateAccessToken(jwtResponse.getAccessToken())
                        .then(jwtProvider.getUserIdFromToken(jwtResponse.getAccessToken()))
                        .flatMap(userId -> userService.createUser(userId, request.getLogin()))
                        .then(Mono.just(new ResponseEntity<>(jwtResponse, HttpStatus.CREATED)))
                ).log();
    }

    @Operation(summary = "User login")
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<JwtResponse>> login(@Valid @RequestBody JwtRequest request) {
        return authService.login(request)
                .flatMap(jwtProvider::validateAccessToken)
                .map(jwtResponse -> new ResponseEntity<>(jwtResponse, HttpStatus.CREATED))
                .log();
    }

    @Operation(summary = "Update token if expired")
    @PostMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<JwtResponse>> updateToken(@RequestBody RefreshJwtRequest request) {
        return authService.updateToken(request)
                .flatMap(jwtProvider::validateAccessToken)
                .map(jwtResponse -> new ResponseEntity<>(jwtResponse, HttpStatus.CREATED))
                .log();
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Delete User with data")
    @DeleteMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> delete(@Parameter(hidden = true) @RequestHeader(AUTHORIZATION) String bearer) {
        return jwtProvider.getTokenFromBearer(bearer)
                .flatMap(accessToken -> jwtProvider
                        .validateAccessToken(accessToken)
                        .then(authService.delete(bearer))
                        .then(jwtProvider.getUserIdFromToken(accessToken))
                        .flatMap(userService::deleteUserById))
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .log();
    }
}
