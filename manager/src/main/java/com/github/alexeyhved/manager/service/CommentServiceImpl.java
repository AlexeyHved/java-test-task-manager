package com.github.alexeyhved.manager.service;

import com.github.alexeyhved.manager.dto.CommentRequest;
import com.github.alexeyhved.manager.dto.CommentResponse;
import com.github.alexeyhved.manager.entity.CommentEntity;
import com.github.alexeyhved.manager.entity.Role;
import com.github.alexeyhved.manager.entity.TaskEntity;
import com.github.alexeyhved.manager.entity.UserEntity;
import com.github.alexeyhved.manager.exception.ResourceNotFoundException;
import com.github.alexeyhved.manager.repo.CommentRepo;
import com.github.alexeyhved.manager.repo.TaskRepo;
import com.github.alexeyhved.manager.repo.UserRepo;
import com.github.alexeyhved.manager.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepo commentRepo;
    private final UserRepo userRepo;
    private final TaskRepo taskRepo;

    @Override
    public Mono<CommentResponse> create(Long userId, Long taskId, CommentRequest commentRequest) {
        Mono<UserEntity> userEntityMono = userRepo.findAdminOrUserExecutor(userId, taskId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format("Admin or executor with id %s not found", userId))));

        Mono<TaskEntity> taskEntityMono = taskRepo.findById(taskId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format("Task with id %s not found", taskId))));

        return taskEntityMono
                .flatMap(taskEntity -> userEntityMono
                        .flatMap(userEntity -> {
                            CommentEntity commentEntity = new CommentEntity();
                            commentEntity.setId(null);
                            commentEntity.setAuthorId(userEntity.getId());
                            commentEntity.setTaskId(taskEntity.getId());
                            commentEntity.setContent(commentRequest.getContent());
                            return commentRepo.save(commentEntity);
                        }))
                .map(Mapper::toCommentResp)
                .log();
    }

    @Override
    public Mono<CommentResponse> update(Long userId, Long taskId, Long commentId, CommentRequest commentRequest) {
        Mono<UserEntity> userEntityMono = userRepo.findAdminOrUserExecutor(userId, taskId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format("Admin or executor with id %s not found", userId))));

        Mono<CommentEntity> commentEntityMono = commentRepo.findByIdAndTaskId(commentId, taskId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format("Comment with id %s not found", commentId))));

        return userEntityMono
                .flatMap(userEntity -> commentEntityMono
                        .flatMap(commentEntity -> {
                            commentEntity.setContent(commentRequest.getContent());
                            if (Objects.equals(userEntity.getId(), commentEntity.getAuthorId())) {
                                commentEntity.setAuthorId(userEntity.getId());
                            }
                            return commentRepo.save(commentEntity);
                        }))
                .map(Mapper::toCommentResp);
    }

    @Override
    public Mono<Void> delete(Long userId, Long taskId, Long commentId) {
        Mono<UserEntity> userEntityMono = userRepo.findAdminOrUserExecutor(userId, taskId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format("Admin or executor with id %s not found", userId))));

        Mono<CommentEntity> commentEntityMono = commentRepo.findByIdAndTaskId(commentId, taskId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format("Comment with id %s not found", commentId))));

        return userEntityMono
                .flatMap(userEntity -> commentEntityMono
                        .flatMap(commentEntity -> {
                            if (Objects.equals(userEntity.getId(), commentEntity.getAuthorId())
                                    || userEntity.getRole().equals(Role.ADMIN)) {
                                return commentRepo.delete(commentEntity);
                            }
                            return Mono.error(new ResourceNotFoundException(String.format("Comment with id %s not found", commentId)));
                        }));
    }
}
