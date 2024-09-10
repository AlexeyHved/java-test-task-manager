package com.github.alexeyhved.manager.util;

import com.github.alexeyhved.manager.dto.*;
import com.github.alexeyhved.manager.entity.CommentEntity;
import com.github.alexeyhved.manager.entity.TaskEntity;
import com.github.alexeyhved.manager.entity.UserEntity;

import java.util.Collections;
import java.util.List;

public class Mapper {
    private Mapper() {}

    public static CommentResponse toCommentResp(CommentEntity commentEntity) {
        return new CommentResponse(commentEntity.getAuthorId(), commentEntity.getTaskId(), commentEntity.getContent());
    }

    public static UserAuthor toUserAuthor(UserEntity userEntity) {
        return new UserAuthor(userEntity.getId(), userEntity.getLogin());
    }

    public static UserExecutor toUserExecutor(UserEntity userEntity) {
        return new UserExecutor(userEntity.getId(), userEntity.getLogin());
    }

    public static TaskResponse toTaskResponse(TaskEntity taskEntity, UserAuthor author) {
        return TaskResponse.builder()
                .id(taskEntity.getId())
                .title(taskEntity.getTitle())
                .description(taskEntity.getDescription())
                .executors(Collections.emptyList())
                .priority(taskEntity.getPriority())
                .author(author)
                .status(taskEntity.getStatus())
                .comments(Collections.emptyList())
                .build();
    }

    public static TaskResponse toTaskResponse(TaskEntity taskEntity,
                                              UserAuthor author,
                                              List<UserExecutor> executorsList,
                                              List<CommentResponse> comments) {
        return TaskResponse.builder()
                .id(taskEntity.getId())
                .title(taskEntity.getTitle())
                .description(taskEntity.getDescription())
                .executors(executorsList)
                .priority(taskEntity.getPriority())
                .author(author)
                .status(taskEntity.getStatus())
                .comments(comments)
                .build();
    }
}
