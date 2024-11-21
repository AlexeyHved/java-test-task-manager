package com.github.alexeyhved.manager.repo;

import com.github.alexeyhved.manager.dto.UserAdmin;
import com.github.alexeyhved.manager.dto.UserExecutor;
import com.github.alexeyhved.manager.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepo extends R2dbcRepository<UserEntity, Long> {
    Mono<UserEntity> findByLogin(String login);

    @Query("select u.id, u.login, u.role from users u where u.id = :id and u.role = 'ADMIN'")
    Mono<UserEntity> findAdminById(Long id);

    @Query("select u.id, u.login, u.role from users u where u.id = :id and u.role = 'USER'")
    Mono<UserEntity> findUserById(Long id);

    @Query("select u.id, u.login, u.role" +
            " from managerdb.public.users u" +
            " join public.tasks t on u.id = t.author_id " +
            "where t.id = :taskId and t.author_id = :userId and u.role = 'ADMIN'")
    Mono<UserEntity> findAuthorByTaskId(Long taskId, Long userId);

    @Query("select u.id, u.login, u.role " +
            "from managerdb.public.users u " +
            "join public.executors_tasks et on u.id = et.executor_id " +
            "where et.task_id = :taskId")
    Flux<UserExecutor> findExecutorsByTaskId(Long taskId);

    @Query("select u.id, u.login, u.role " +
            "from public.users u " +
            "left join public.executors_tasks et on u.id = et.executor_id " +
            "where (et.executor_id = :userId and et.task_id = :taskId) " +
            "or (u.id = :userId and u.role = 'ADMIN')")
    Mono<UserEntity> findAdminOrUserExecutor(Long userId, Long taskId);
}
