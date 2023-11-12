package com.example.authservice.dto.request;

import com.example.authservice.validation.AlgorithmAndKeySize;
import com.example.authservice.validation.CountryCode;
import com.example.authservice.validation.NoHTMLTags;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@AlgorithmAndKeySize
public class CSRCreationRequest {

    @NotBlank(message = "Organization name cannot be blank")
    @NoHTMLTags
    private String organization;

    @NotBlank(message = "City cannot be blank")
    @NoHTMLTags
    private String city;

    @NotBlank(message = "State cannot be blank")
    @NoHTMLTags
    private String state;

    @NotBlank(message = "Country code cannot be blank")
    @CountryCode
    @NoHTMLTags
    private String country;

    @NotBlank(message = "Algorithm cannot be blank")
    @Pattern(regexp = "^(RSA|DSA|EC)$", message = "Invalid algorithm. Allowed algorithms are RSA, DSA and EC.")
    @NoHTMLTags
    private String algorithm;

    @NotNull
    @Positive(message = "Keysize must be a positive integer.")
    private int keySize;

}
