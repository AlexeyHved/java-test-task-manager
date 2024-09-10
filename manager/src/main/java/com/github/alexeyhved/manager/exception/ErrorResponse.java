package com.github.alexeyhved.manager.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Builder
@Data
public class ErrorResponse {
    private HttpStatus status;
    private String reason;
    private String message;
    private String timestamp;
}
