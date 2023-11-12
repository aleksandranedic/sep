package com.example.authservice.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;

import java.util.Map;

@Getter
public class ResponseError {
    private final Integer status;
    private final String message;
    @JsonInclude(Include.NON_NULL)
    private final Map<String, String> errors;

    public ResponseError(Integer status, String message) {
        this.status = status;
        this.message = message;
        this.errors = null;
    }
}
