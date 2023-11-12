package com.example.authservice.exception;

public class BlacklistedTokenException extends InvalidAccessTokenException {
    public BlacklistedTokenException(String message) {
        super(message);
    }

    public BlacklistedTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
