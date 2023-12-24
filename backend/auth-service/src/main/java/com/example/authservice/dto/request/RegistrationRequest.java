package com.example.authservice.dto.request;

import com.example.authservice.validation.PasswordsMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@PasswordsMatch
public class RegistrationRequest extends UserDetailsRequest {
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{12,}$",
            message = "Password must have at least 12 characters, at least one uppercase letter, " +
                    "one lowercase letter, one digit, and one special character.")
    private String password;

    @NotBlank
    private String passwordConfirmation;
}
