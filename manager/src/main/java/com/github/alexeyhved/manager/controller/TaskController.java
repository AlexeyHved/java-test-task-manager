package com.github.alexeyhved.manager.controller;

import com.github.alexeyhved.manager.dto.TaskRequest;
import com.github.alexeyhved.manager.dto.TaskResponse;
import com.github.alexeyhved.manager.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.github.alexeyhved.manager.util.Const.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
@Validated
public class TaskController {
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final TaskService taskService;

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "New task creating")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<TaskResponse>> createTask(@Parameter(hidden = true) @RequestHeader(AUTHORIZATION) String bearer,
                                                         @Valid @RequestBody TaskRequest taskRequest) {

        Mono<TaskResponse> taskResponseMono = jwtProvider.getTokenFromBearer(bearer)
                .flatMap(token -> jwtProvider
                        .validateAccessToken(token)
                        .then(jwtProvider.getUserIdFromToken(token))
                        .flatMap(userService::findAuthorUserById)
                        .flatMap(userAuthor -> taskService.createTask(taskRequest, userAuthor)))
                .log();
        return new ResponseEntity<>(taskResponseMono, HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Find task by id")
    @GetMapping(value = "/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<TaskResponse>> getTask(@Parameter(hidden = true) @RequestHeader(AUTHORIZATION) String bearer,
                                                      @PathVariable @Positive Long taskId) {

        Mono<TaskResponse> taskResponseMono = jwtProvider.getTokenFromBearer(bearer)
                .map(jwtProvider::validateAccessToken)
                .then(taskService.findTaskById(taskId))
                .log();
        return new ResponseEntity<>(taskResponseMono, HttpStatus.OK);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = " Find all tasks")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Flux<TaskResponse>> getTasks(@Parameter(hidden = true) @RequestHeader(AUTHORIZATION) String bearer) {
        Flux<TaskResponse> taskResponseFlux = jwtProvider.getTokenFromBearer(bearer)
                .flatMap(jwtProvider::validateAccessToken)
                .thenMany(taskService.findAllTasks())
                .log();

        return new ResponseEntity<>(taskResponseFlux, HttpStatus.OK);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Find tasks pageable")
    @GetMapping(value = "/pageable", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<Page<TaskResponse>>> getTasks(@Parameter(hidden = true) @RequestHeader(AUTHORIZATION) String bearer,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Mono<Page<TaskResponse>> pageMono = jwtProvider.getTokenFromBearer(bearer)
                .flatMap(jwtProvider::validateAccessToken)
                .then(taskService.findAllTasksPageable(pageable))
                .log();

        return new ResponseEntity<>(pageMono, HttpStatus.OK);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Add executor on task")
    @PatchMapping(value = "/{taskId}/executors", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<TaskResponse>> addExecutor(@Parameter(hidden = true) @RequestHeader(AUTHORIZATION) String bearer,
                                                          @Positive @PathVariable Long taskId,
                                                          @RequestParam String executor) {

        Mono<TaskResponse> taskResponseMono = jwtProvider.getTokenFromBearer(bearer)
                .flatMap(jwtProvider::validateAccessToken)
                .flatMap(jwtProvider::getUserIdFromToken)
                .flatMap(userId -> userService.findAuthorByTaskIdAndUserId(taskId, userId))
                .flatMap(userAuthor -> userService.findExecutorByLogin(executor)
                        .flatMap(userExecutor -> taskService.addExecutor(taskId, userAuthor, userExecutor)))
                .log();

        return new ResponseEntity<>(taskResponseMono, HttpStatus.OK);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Update task by author")
    @PatchMapping(value = "/{taskId}/authors", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<TaskResponse>> updateTaskByAuthor(
            @Parameter(hidden = true) @RequestHeader(AUTHORIZATION) String bearer,
            @Positive @PathVariable Long taskId,
            @RequestParam(required = false, defaultValue = "") String title,
            @RequestParam(required = false, defaultValue = "") String description,
            @RequestParam(required = false, defaultValue = "") String status,
            @RequestParam(required = false, defaultValue = "") String priority
    ) {
        Mono<TaskResponse> taskResponseMono = jwtProvider.getTokenFromBearer(bearer)
                .flatMap(token -> jwtProvider.validateAccessToken(token)
                        .then(jwtProvider.getUserIdFromToken(token)))
                .flatMap(authorId -> taskService.updateTaskByAuthor(authorId, taskId, title, description, status, priority))
                .log();

        return new ResponseEntity<>(taskResponseMono, HttpStatus.OK);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Update task by executor")
    @PatchMapping(value = "/{taskId}/executors/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<TaskResponse>> updateTaskByExecutor(@Parameter(hidden = true) @RequestHeader(AUTHORIZATION) String bearer,
                                                                   @Positive @PathVariable Long taskId,
                                                                   @NotBlank @PathVariable String status) {

        Mono<TaskResponse> taskResponseMono = jwtProvider.getTokenFromBearer(bearer)
                .flatMap(token -> jwtProvider.validateAccessToken(token)
                        .then(jwtProvider.getUserIdFromToken(token)))
                .flatMap(executorId -> taskService.updateTaskByExecutor(executorId, taskId, status))
                .log();

        return new ResponseEntity<>(taskResponseMono, HttpStatus.OK);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Delete task by id")
    @DeleteMapping(value = "/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<Void>> deleteTaskById(@Parameter(hidden = true) @RequestHeader(AUTHORIZATION) String bearer,
                                                     @Positive @PathVariable Long taskId) {

        Mono<Void> monoEmpty = jwtProvider.getTokenFromBearer(bearer)
                .flatMap(token -> jwtProvider.validateAccessToken(token)
                        .then(jwtProvider.getUserIdFromToken(token)))
                .flatMap(authorId -> taskService.deleteTaskByIdAndAuthor(taskId, authorId))
                .log();

        return new ResponseEntity<>(monoEmpty, HttpStatus.NO_CONTENT);
    }
}
