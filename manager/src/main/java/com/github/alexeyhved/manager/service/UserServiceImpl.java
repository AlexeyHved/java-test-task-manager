package com.github.alexeyhved.manager.service;

import com.github.alexeyhved.manager.dto.UserAuthor;
import com.github.alexeyhved.manager.dto.UserExecutor;
import com.github.alexeyhved.manager.entity.UserEntity;
import com.github.alexeyhved.manager.exception.ResourceNotFoundException;
import com.github.alexeyhved.manager.repo.UserRepo;
import com.github.alexeyhved.manager.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;

    @Override
    public Mono<UserAuthor> createUser(Long id, String login) {
            UserEntity userEntity = new UserEntity();
            userEntity.setId(id);
            userEntity.setLogin(login);

        return userRepo.save(userEntity)
                .switchIfEmpty(Mono.error(new RuntimeException("Error on create user")))
                .map(Mapper::toUserAuthor);
    }

    public Mono<Void> deleteUserById(Long userId) {
        return userRepo.deleteById(userId);
    }

    @Override
    public Mono<UserAuthor> findAuthorUserById(Long userId) {
        return userRepo.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User by id not found")))
                .map(Mapper::toUserAuthor);
    }

    @Override
    public Mono<UserExecutor> findExecutorByLogin(String executorLogin) {
        return userRepo.findByLogin(executorLogin)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User by login not found")))
                .map(Mapper::toUserExecutor);
    }

    @Override
    public Mono<UserAuthor> findAuthorByTaskIdAndUserId(Long taskId, Long userId) {
        return userRepo.findAuthorByTaskId(taskId, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Author or task not found")))
                .map(Mapper::toUserAuthor);
    }
}
