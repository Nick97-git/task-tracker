package com.dev.tracker.exception;

public class InvalidJwtAuthenticationException extends Exception {

    public InvalidJwtAuthenticationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
