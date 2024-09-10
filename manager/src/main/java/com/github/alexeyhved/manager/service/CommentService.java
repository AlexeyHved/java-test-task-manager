package com.github.alexeyhved.manager.service;

import com.github.alexeyhved.manager.dto.CommentRequest;
import com.github.alexeyhved.manager.dto.CommentResponse;
import reactor.core.publisher.Mono;

public interface CommentService {
    Mono<CommentResponse> create(Long authorId, Long taskId, CommentRequest commentRequest);

    Mono<CommentResponse> update(Long authorId, Long taskId, Long commentId, CommentRequest commentRequest);

    Mono<Void> delete(Long authorId, Long taskId, Long commentId);
}
