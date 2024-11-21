package com.github.alexeyhved.manager.service;

import com.github.alexeyhved.manager.dto.UserAdmin;
import com.github.alexeyhved.manager.dto.UserExecutor;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserAdmin> createAdmin(Long id, String login);

    Mono<UserAdmin> createUser(Long userId, String login);

    Mono<Void> deleteUserById(Long userId);

    Mono<UserAdmin> findAdminById(Long userId);

    Mono<UserExecutor> findExecutorByLogin(String executorLogin);

    Mono<UserAdmin> findAuthorByTaskIdAndUserId(Long taskId, Long userId);
}
