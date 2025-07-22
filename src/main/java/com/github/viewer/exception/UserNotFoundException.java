package com.github.viewer.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String user) {
        super("User '%s' not found".formatted(user));
    }
}