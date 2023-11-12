package com.example.authservice.exception;

public class CertificateAlreadyRevokedException extends RuntimeException {
    public CertificateAlreadyRevokedException() {
    }

    public CertificateAlreadyRevokedException(String message) {
        super(message);
    }

    public CertificateAlreadyRevokedException(String message, Throwable cause) {
        super(message, cause);
    }
}
