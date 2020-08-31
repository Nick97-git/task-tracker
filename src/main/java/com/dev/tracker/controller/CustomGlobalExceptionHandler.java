package com.dev.tracker.controller;

import com.dev.tracker.exception.AuthenticationException;
import com.dev.tracker.exception.DeleteUserException;
import com.dev.tracker.exception.NoSuchUserException;
import com.dev.tracker.exception.TaskStatusException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        body.put("errors", errors);
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(
            AuthenticationException exception) {
        return getResponseEntity(exception.getMessage());
    }

    @ExceptionHandler(TaskStatusException.class)
    public ResponseEntity<Object> handleTaskStatusException(
            TaskStatusException exception) {
        return getResponseEntity(exception.getMessage());
    }

    @ExceptionHandler(DeleteUserException.class)
    public ResponseEntity<Object> handleDeleteUserException(
            DeleteUserException exception) {
        return getResponseEntity(exception.getMessage());
    }

    @ExceptionHandler(NoSuchUserException.class)
    public ResponseEntity<Object> handleNoSuchUserException(
            NoSuchUserException exception) {
        return getResponseEntity(exception.getMessage());
    }

    private ResponseEntity<Object> getResponseEntity(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("error", message);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
