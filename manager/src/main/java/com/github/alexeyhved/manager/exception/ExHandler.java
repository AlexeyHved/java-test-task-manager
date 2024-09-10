package com.github.alexeyhved.manager.exception;


import com.github.alexeyhved.manager.util.Const;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ExHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> ExceptionWrapperHandler(final ExceptionWrapper e) {
        ErrorResponse errorResponse = e.getErrorResponse();
        log.warn(errorResponse.toString());
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse constraintViolationHandler(final ConstraintViolationException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Validation error")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(Const.DTF))
                .build();

        log.warn(e.toString());
        return errorResponse;
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundExceptionHandler(final ResourceNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason("Resource not found")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(Const.DTF))
                .build();

        log.warn(e.toString());
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse illegalArgumentExceptionHandler(final IllegalArgumentException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason("Illegal Argument")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(Const.DTF))
                .build();

        log.warn(e.toString());
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse dataIntegrityHandler(final DataIntegrityViolationException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT)
                .reason("Database error")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(Const.DTF))
                .build();

        log.warn(e.toString());
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse resAlreayExist(final ResourceAlreadyExistEx e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT)
                .reason("Already exist")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(Const.DTF))
                .build();

        log.warn(e.toString());
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse resourceAccessExceptionHandler(final ResourceAccessException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT)
                .reason("No Access")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(Const.DTF))
                .build();

        log.warn(e.toString());
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentNotValidHandler(final MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .reason("MethodArgumentNotValidException")
                .timestamp(LocalDateTime.now().format(Const.DTF))
                .build();
        log.warn(e.toString());
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse jwtExceptionHandler(final JwtException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .reason("Jwt Exception")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(Const.DTF))
                .build();
        log.warn(e.toString());
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse webExchangeBindExceptionHandler(final WebExchangeBindException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason(e.getReason())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(Const.DTF))
                .build();
        log.warn(e.toString());
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .reason("Oops, an unexpected error has occurred")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(Const.DTF))
                .build();

        log.warn(e.toString());
        return errorResponse;
    }
}
