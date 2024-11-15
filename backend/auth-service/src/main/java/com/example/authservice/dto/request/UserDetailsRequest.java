package com.example.authservice.dto.request;

import com.example.authservice.validation.NoHTMLTags;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDetailsRequest {
    @Email
    @NoHTMLTags
    private String email;

    @NotBlank
    @Size(max = 40, message = "First name cannot be longer than 40 characters.")
    @NoHTMLTags
    private String firstName;

    @NotBlank
    @Size(max = 40, message = "Last name cannot be longer than 40 characters.")
    @NoHTMLTags
    private String lastName;

    @Pattern(regexp = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$",
            message = "Phone number is not in valid format.")
    @NoHTMLTags
    private String phoneNumber;

    @NotBlank
    @Size(max = 40, message = "City name cannot be longer than 40 characters.")
    @NoHTMLTags
    private String city;
}
