package com.example.authservice.exception;

import com.example.authservice.model.User;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

public class Invalid2FaCodeException extends AuthenticationException {

    @Getter
    private User user;

    public Invalid2FaCodeException(String message, User user) {
        super(message);
        this.user = user;
    }

    public Invalid2FaCodeException(String message, User user, Throwable cause) {
        super(message, cause);
        this.user = user;
    }
}
