package com.github.alexeyhved.manager.exception;


import lombok.*;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ExceptionWrapper extends Throwable {
    private ErrorResponse errorResponse;
}
