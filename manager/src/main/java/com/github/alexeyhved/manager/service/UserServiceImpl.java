package com.github.alexeyhved.manager.service;

import com.github.alexeyhved.manager.dto.UserAdmin;
import com.github.alexeyhved.manager.dto.UserExecutor;
import com.github.alexeyhved.manager.entity.Role;
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
    public Mono<UserAdmin> createAdmin(Long id, String login) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setLogin(login);
        userEntity.setRole(Role.ADMIN);

        return userRepo.save(userEntity)
                .switchIfEmpty(Mono.error(new RuntimeException("Error on create user")))
                .map(Mapper::toUserAdmin);
    }

    @Override
    public Mono<UserAdmin> createUser(Long id, String login) {
            UserEntity userEntity = new UserEntity();
            userEntity.setId(id);
            userEntity.setLogin(login);
            userEntity.setRole(Role.USER);

        return userRepo.save(userEntity)
                .switchIfEmpty(Mono.error(new RuntimeException("Error on create user")))
                .map(Mapper::toUserAdmin);
    }

    @Override
    public Mono<Void> deleteUserById(Long userId) {
        return userRepo.deleteById(userId);
    }

    @Override
    public Mono<UserAdmin> findAdminById(Long userId) {
        return userRepo.findAdminById(userId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Admin by id not found")))
                .map(Mapper::toUserAdmin);
    }

    @Override
    public Mono<UserExecutor> findExecutorByLogin(String executorLogin) {
        return userRepo.findByLogin(executorLogin)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User by login not found")))
                .map(Mapper::toUserExecutor);
    }

    @Override
    public Mono<UserAdmin> findAuthorByTaskIdAndUserId(Long taskId, Long userId) {
        return userRepo.findAuthorByTaskId(taskId, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Author or task not found")))
                .map(Mapper::toUserAdmin);
    }
}
