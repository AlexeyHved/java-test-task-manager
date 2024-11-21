package com.github.alexeyhved.manager.dto;


import com.github.alexeyhved.manager.entity.Priority;
import com.github.alexeyhved.manager.entity.Status;
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
    private UserAdmin author;
    @Builder.Default
    private List<UserExecutor> executors = Collections.emptyList();
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    @Builder.Default
    private List<CommentResponse> comments = Collections.emptyList();
}
