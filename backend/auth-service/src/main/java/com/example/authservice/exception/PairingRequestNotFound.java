package com.example.authservice.exception;

public class PairingRequestNotFound extends RuntimeException {
    public PairingRequestNotFound() {
    }

    public PairingRequestNotFound(String message) {
        super(message);
    }

    public PairingRequestNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
