package com.github.alexeyhved.manager.controller;

import com.github.alexeyhved.manager.dto.CommentRequest;
import com.github.alexeyhved.manager.dto.CommentResponse;
import com.github.alexeyhved.manager.service.CommentService;
import com.github.alexeyhved.manager.service.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
@Validated
@RequestMapping("/tasks")
public class CommentController {
    private final CommentService commentService;
    private final JwtProvider jwtProvider;

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Create comment")
    @PostMapping(value = "/{taskId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<CommentResponse>> createComment(@Parameter(hidden = true) @RequestHeader(AUTHORIZATION) String bearer,
                                                               @Positive @PathVariable Long taskId,
                                                               @Valid @RequestBody CommentRequest commentRequest) {

        Mono<CommentResponse> commentRespMono = jwtProvider.getTokenFromBearer(bearer)
                .flatMap(token -> jwtProvider.validateAccessToken(token)
                        .then(jwtProvider.getUserIdFromToken(token)))
                .flatMap(authorId -> commentService.create(authorId, taskId, commentRequest))
                .log();

        return new ResponseEntity<>(commentRespMono, HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Update comment by author")
    @PutMapping(value = "/{taskId}/comments/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<CommentResponse>> updateComment(@Parameter(hidden = true) @RequestHeader(AUTHORIZATION) String bearer,
                                                               @Positive @PathVariable Long taskId,
                                                               @Positive @PathVariable Long commentId,
                                                               @Valid @RequestBody CommentRequest commentRequest) {

        Mono<CommentResponse> commentRespMono = jwtProvider.getTokenFromBearer(bearer)
                .flatMap(token -> jwtProvider.validateAccessToken(token)
                        .then(jwtProvider.getUserIdFromToken(token)))
                .flatMap(authorId -> commentService.update(authorId, taskId, commentId, commentRequest))
                .log();

        return new ResponseEntity<>(commentRespMono, HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Delete comment")
    @DeleteMapping(value = "/{taskId}/comments/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<Void>> deleteComment(@Parameter(hidden = true) @RequestHeader(AUTHORIZATION) String bearer,
                                                           @Positive @PathVariable Long taskId,
                                                    @Positive @PathVariable Long commentId) {

        Mono<Void> monoEmpty = jwtProvider.getTokenFromBearer(bearer)
                .flatMap(token -> jwtProvider.validateAccessToken(token)
                        .then(jwtProvider.getUserIdFromToken(token)))
                .flatMap(authorId -> commentService.delete(authorId, taskId, commentId))
                .log();

        return new ResponseEntity<>(monoEmpty, HttpStatus.NO_CONTENT);
    }
}
