package com.github.alexeyhved.manager.service;

import com.github.alexeyhved.manager.dto.CommentRequest;
import com.github.alexeyhved.manager.dto.CommentResponse;
import com.github.alexeyhved.manager.entity.CommentEntity;
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

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepo commentRepo;
    private final UserRepo userRepo;
    private final TaskRepo taskRepo;

    @Override
    public Mono<CommentResponse> create(Long authorId, Long taskId, CommentRequest commentRequest) {
        Mono<UserEntity> userEntityMono = userRepo.findById(authorId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format("User with id %s not found", authorId))));

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
    public Mono<CommentResponse> update(Long authorId, Long taskId, Long commentId, CommentRequest commentRequest) {

        Mono<CommentEntity> commentEntityMono = commentRepo.findByIdAndAuthorIdAndTaskId(commentId, authorId, taskId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format("Comment with id %s not found", commentId))));

        return commentEntityMono.map(commentEntity -> {
                    commentEntity.setContent(commentRequest.getContent());
                    return commentEntity;
                })
                .flatMap(commentRepo::save)
                .map(Mapper::toCommentResp);
    }

    @Override
    public Mono<Void> delete(Long authorId, Long taskId, Long commentId) {
        return commentRepo.deleteByIdAndAuthorIdAndTaskId(commentId, authorId, taskId)
                .flatMap(isSuccess -> isSuccess ?
                        Mono.empty() : Mono.error(new ResourceNotFoundException(String.format("Comment with id %s not found", commentId))));
    }
}
