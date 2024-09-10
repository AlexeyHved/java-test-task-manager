package com.github.alexeyhved.manager.repo;

import com.github.alexeyhved.manager.dto.UserAuthor;
import com.github.alexeyhved.manager.dto.UserExecutor;
import com.github.alexeyhved.manager.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserRepo extends R2dbcRepository<UserEntity, Long> {
    Mono<UserEntity> findByLogin(String login);

    @Query("select u.id, u.login" +
            " from managerdb.public.users u" +
            " join public.tasks t on u.id = t.author_id " +
            "where t.id = :taskId and t.author_id = :userId")
    Mono<UserEntity> findAuthorByTaskId(Long taskId, Long userId);

    @Query("select u.id, u.login " +
            "from managerdb.public.users u " +
            "join public.executors_tasks et on u.id = et.executor_id " +
            "where et.task_id = :taskId")
    Flux<UserExecutor> findExecutorsByTaskId(Long taskId);


    @Query("select u.id, u.login" +
            " from managerdb.public.users" +
            " u join public.tasks t on u.id = t.author_id" +
            " where t.author_id = :authorId and t.id = :taskId")
    Mono<UserAuthor> findByAuthorIdAndTaskId(Long authorId, Long taskId);
}
