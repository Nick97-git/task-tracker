package com.dev.tracker.exception;

public class InvalidJwtAuthenticationException extends Throwable {

    public InvalidJwtAuthenticationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
