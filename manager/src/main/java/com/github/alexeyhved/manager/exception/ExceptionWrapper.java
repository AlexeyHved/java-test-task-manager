package com.github.alexeyhved.manager.exception;


import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ExceptionWrapper extends Throwable {
    private ErrorResponse errorResponse;
}
