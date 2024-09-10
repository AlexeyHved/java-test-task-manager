package com.github.alexeyhved.manager.repo;

import com.github.alexeyhved.manager.entity.TaskEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskRepo extends R2dbcRepository<TaskEntity, Long> {

    @Modifying
    @Query("insert into executors_tasks (executor_id, task_id) values (:executorId, :taskId)")
    Mono<Void> addLinkToExecutorsTasks(Long executorId, Long taskId);

    Mono<Integer> countTaskEntitiesById(Long taskId);

    Mono<TaskEntity> findByIdAndAuthorId(Long taskId, Long authorId);

    @Query("select t.* from tasks t " +
            "join public.executors_tasks et on t.id = et.task_id " +
            "where et.task_id = :taskId and et.executor_id = :executorId")
    Mono<TaskEntity> findByIdAndExecutorId(Long taskId, Long executorId);

    Flux<TaskEntity> findAllBy(Pageable pageable);

    Mono<Boolean> deleteByIdAndAuthorId(Long taskId, Long authorId);
}
