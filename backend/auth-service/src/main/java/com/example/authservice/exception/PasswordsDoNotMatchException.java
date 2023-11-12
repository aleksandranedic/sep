package com.example.authservice.exception;

public class PasswordsDoNotMatchException  extends RuntimeException {
    public PasswordsDoNotMatchException() {
    }

    public PasswordsDoNotMatchException(String message) {
        super(message);
    }

    public PasswordsDoNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
