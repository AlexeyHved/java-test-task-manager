package com.github.alexeyhved.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentResponse {
    private long authorId;
    private long taskId;
    private String content;
}
