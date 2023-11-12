package com.example.authservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CertificateCreationOptions {

    @Valid
    private Extensions extensions;

    @NotNull
    private String issuerAlias;
}
