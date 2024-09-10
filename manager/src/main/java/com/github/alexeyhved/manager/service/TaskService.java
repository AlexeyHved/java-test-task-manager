package com.github.alexeyhved.manager.service;

import com.github.alexeyhved.manager.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskService {
    Mono<TaskResponse> createTask(TaskRequest taskRequest, UserAuthor author);

    Mono<TaskResponse> addExecutor(Long taskId, UserAuthor userAuthor, UserExecutor userExecutor);

    Mono<TaskResponse> updateTaskByAuthor(Long authorId, Long taskId, String title, String description, String status, String priority);

    Mono<TaskResponse> findTaskById(Long taskId);

    Flux<TaskResponse> findAllTasks();

    Mono<Page<TaskResponse>>findAllTasksPageable(Pageable pageable);

    Mono<TaskResponse> updateTaskByExecutor(Long executorId, Long taskId, String status);

    Mono<Void> deleteTaskByIdAndAuthor(Long taskId, Long authorId);
}

