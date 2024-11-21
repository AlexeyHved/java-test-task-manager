package com.github.alexeyhved.manager.repo;

import com.github.alexeyhved.manager.entity.CommentEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentRepo extends R2dbcRepository<CommentEntity, Long> {
    Flux<CommentEntity> findByTaskId(Long taskId);

    Mono<CommentEntity> findByIdAndTaskId(Long id, Long taskId);

}
