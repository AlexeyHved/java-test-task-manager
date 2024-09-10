package com.github.alexeyhved.manager.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class TaskResponse {
    private Long id;
    private UserAuthor author;
    @Builder.Default
    private List<UserExecutor> executors = Collections.emptyList();
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    @Builder.Default
    private List<CommentResponse> comments = Collections.emptyList();
}
