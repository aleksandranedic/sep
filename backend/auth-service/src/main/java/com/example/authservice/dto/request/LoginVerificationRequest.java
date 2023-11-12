package com.example.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginVerificationRequest {

    @NotBlank
    private String password;
}
