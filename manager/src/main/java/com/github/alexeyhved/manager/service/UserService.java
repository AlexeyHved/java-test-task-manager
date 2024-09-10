package com.github.alexeyhved.manager.service;

import com.github.alexeyhved.manager.dto.UserAuthor;
import com.github.alexeyhved.manager.dto.UserExecutor;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserAuthor> createUser(Long userId, String login);

    Mono<Void> deleteUserById(Long userId);

    Mono<UserAuthor> findAuthorUserById(Long userId);

    Mono<UserExecutor> findExecutorByLogin(String executorLogin);

    Mono<UserAuthor> findAuthorByTaskIdAndUserId(Long taskId, Long userId);
}
