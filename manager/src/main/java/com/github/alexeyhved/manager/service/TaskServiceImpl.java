package com.github.alexeyhved.manager.service;

import com.github.alexeyhved.manager.dto.*;
import com.github.alexeyhved.manager.entity.Priority;
import com.github.alexeyhved.manager.entity.Status;
import com.github.alexeyhved.manager.entity.TaskEntity;
import com.github.alexeyhved.manager.entity.UserEntity;
import com.github.alexeyhved.manager.exception.ResourceNotFoundException;
import com.github.alexeyhved.manager.repo.CommentRepo;
import com.github.alexeyhved.manager.repo.TaskRepo;
import com.github.alexeyhved.manager.repo.UserRepo;
import com.github.alexeyhved.manager.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepo taskRepo;
    private final UserRepo userRepo;
    private final CommentRepo commentRepo;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Mono<TaskResponse> createTask(TaskRequest taskRequest, UserAdmin author) {
        TaskEntity taskEntity = new TaskEntity(
                null,
                author.getId(),
                taskRequest.getTitle(),
                taskRequest.getDescription(),
                Status.WAITING,
                taskRequest.getPriority()
        );

        return taskRepo.save(taskEntity)
                .switchIfEmpty(Mono.error(new RuntimeException("Error on creating task")))
                .map(task -> Mapper.toTaskResponse(task, author));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<TaskResponse> addExecutor(Long taskId, UserAdmin userAdmin, UserExecutor userExecutor) {
        Mono<List<CommentResponse>> commentsMonoList = commentRepo.findByTaskId(taskId)
                .map(Mapper::toCommentResp)
                .collectList();

        return taskRepo.findById(taskId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Task not found")))
                .flatMap(taskEntity -> taskRepo
                        .addLinkToExecutorsTasks(userExecutor.getId(), taskEntity.getId())
                        .then(userRepo.findExecutorsByTaskId(taskId).collectList())
                        .flatMap(userExecutors -> commentsMonoList
                                .map(commentsList -> Mapper.toTaskResponse(taskEntity, userAdmin, userExecutors, commentsList))));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<TaskResponse> updateTaskByAuthor(Long authorId,
                                                 Long taskId,
                                                 String title,
                                                 String description,
                                                 String status,
                                                 String priority) {

        Mono<List<CommentResponse>> commentsMonoList = commentRepo.findByTaskId(taskId)
                .map(Mapper::toCommentResp)
                .collectList();

        return taskRepo.findByIdAndAuthorId(taskId, authorId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Task not found")))
                .flatMap(taskEntity -> {
                    if (!title.isBlank()) taskEntity.setTitle(title);
                    if (!description.isBlank()) taskEntity.setDescription(description);
                    if (!status.isBlank()) taskEntity.setStatus(Status.valueOf(status));
                    if (!priority.isBlank()) taskEntity.setPriority(Priority.valueOf(priority));

                    return taskRepo.save(taskEntity)
                            .flatMap(savedTaskEntity -> userRepo.findById(savedTaskEntity.getAuthorId())
                                    .map(Mapper::toUserAdmin)
                                    .flatMap(userAuthor -> userRepo.findExecutorsByTaskId(taskId)
                                            .collectList()
                                            .flatMap(userExecutorsList -> commentsMonoList.map(commentsList -> Mapper
                                                    .toTaskResponse(taskEntity, userAuthor, userExecutorsList, commentsList)))));
                });
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<TaskResponse> findTaskById(Long taskId) {
        Mono<List<CommentResponse>> commentsMonoList = commentRepo.findByTaskId(taskId)
                .map(Mapper::toCommentResp)
                .collectList();

        Mono<List<UserExecutor>> executorsMonoList = userRepo.findExecutorsByTaskId(taskId).collectList();
        Mono<TaskEntity> taskEntityMono = taskRepo.findById(taskId);

        return taskEntityMono
                .flatMap(taskEntity -> userRepo.findAuthorByTaskId(taskId, taskEntity.getAuthorId())
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Task not found")))
                        .map(Mapper::toUserAdmin)
                        .flatMap(userAuthor -> executorsMonoList
                                .flatMap(executors -> commentsMonoList
                                        .map(comments -> Mapper.toTaskResponse(taskEntity, userAuthor, executors, comments)))));

    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Flux<TaskResponse> findAllTasks() {
        return taskRepo.findAll()
                .flatMap(taskEntity -> userRepo.findAuthorByTaskId(taskEntity.getId(), taskEntity.getAuthorId())
                        .map(Mapper::toUserAdmin)
                        .flatMap(userAuthor -> userRepo
                                .findExecutorsByTaskId(taskEntity.getId()).collectList()
                                .flatMap(executors -> commentRepo
                                        .findByTaskId(taskEntity.getId())
                                        .map(Mapper::toCommentResp)
                                        .collectList()
                                        .map(comments -> Mapper.toTaskResponse(taskEntity, userAuthor, executors, comments)))));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<Page<TaskResponse>> findAllTasksPageable(Pageable pageable) {
        return taskRepo.findAllBy(pageable)
                .flatMap(taskEntity -> userRepo.findAuthorByTaskId(taskEntity.getId(), taskEntity.getAuthorId())
                        .map(Mapper::toUserAdmin)
                        .flatMap(userAuthor -> userRepo.findExecutorsByTaskId(taskEntity.getId()).collectList()
                                .flatMap(executors -> commentRepo
                                        .findByTaskId(taskEntity.getId())
                                        .map(Mapper::toCommentResp)
                                        .collectList()
                                        .map(comments -> Mapper.toTaskResponse(taskEntity, userAuthor, executors, comments)))))
                .collectList()
                .zipWith(taskRepo.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<TaskResponse> updateTaskByExecutor(Long executorId, Long taskId, String status) {
        Mono<List<CommentResponse>> commentsMonoList = commentRepo.findByTaskId(taskId)
                .map(Mapper::toCommentResp)
                .collectList();

        return taskRepo.findByIdAndExecutorId(taskId, executorId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Task not found")))
                .flatMap(taskEntity -> {
                    taskEntity.setStatus(Status.valueOf(status));
                    return taskRepo.save(taskEntity);
                })
                .flatMap(taskEntity -> userRepo
                        .findById(taskEntity.getAuthorId())
                        .map(Mapper::toUserAdmin)
                        .flatMap(userAuthor -> userRepo
                                .findExecutorsByTaskId(taskEntity.getId()).collectList()
                                .flatMap(executors -> commentsMonoList
                                        .map(comments -> Mapper.toTaskResponse(taskEntity, userAuthor, executors, comments)))));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<Void> deleteTaskByIdAndAuthor(Long taskId, Long userId) {
        Mono<UserEntity> userEntityMono = userRepo.findAdminById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format("User with id %s not found", userId))));

        Mono<TaskEntity> taskEntityMono = taskRepo.findById(taskId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Task not found")));

        return userEntityMono
                .flatMap(userEntity -> taskEntityMono
                        .flatMap(taskEntity -> taskRepo.deleteById(taskEntity.getId())));
    }

    @Override
    public Flux<TaskResponse> findTasksByExecutor(Long executorId) {
        return taskRepo.findByExecutorId(executorId)
                .flatMap(taskEntity -> userRepo.findAuthorByTaskId(taskEntity.getId(), taskEntity.getAuthorId())
                        .map(Mapper::toUserAdmin)
                        .flatMap(userAuthor -> userRepo
                                .findExecutorsByTaskId(taskEntity.getId()).collectList()
                                .flatMap(executors -> commentRepo
                                        .findByTaskId(taskEntity.getId())
                                        .map(Mapper::toCommentResp)
                                        .collectList()
                                        .map(comments -> Mapper.toTaskResponse(taskEntity, userAuthor, executors, comments)))));


    }

    @Override
    public Flux<TaskResponse> findTasksByAuthor(Long authorId) {
        return taskRepo.findByAuthorId(authorId)
                .flatMap(taskEntity -> userRepo.findAuthorByTaskId(taskEntity.getId(), taskEntity.getAuthorId())
                        .map(Mapper::toUserAdmin)
                        .flatMap(userAuthor -> userRepo
                                .findExecutorsByTaskId(taskEntity.getId()).collectList()
                                .flatMap(executors -> commentRepo
                                        .findByTaskId(taskEntity.getId())
                                        .map(Mapper::toCommentResp)
                                        .collectList()
                                        .map(comments -> Mapper.toTaskResponse(taskEntity, userAuthor, executors, comments)))));
    }

}
