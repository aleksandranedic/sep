package com.example.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.example.authservice.model.Role;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private Long expiresAt;
    private Role role;
}
