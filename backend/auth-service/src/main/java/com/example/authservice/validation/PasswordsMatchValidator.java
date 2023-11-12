package com.example.authservice.validation;

import com.example.authservice.dto.request.RegistrationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, RegistrationRequest> {

    @Override
    public void initialize(PasswordsMatch constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(RegistrationRequest request, ConstraintValidatorContext constraintValidatorContext) {
        return passwordsMatch(request.getPassword(), request.getPasswordConfirmation());
    }

    private Boolean passwordsMatch(String password, String confirmation) {
        return password != null && password.equals(confirmation);
    }
}
