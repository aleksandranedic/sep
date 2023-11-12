package com.example.authservice.dto.request;

import com.example.authservice.validation.NoHTMLTags;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class Extensions {

    @NotNull
    private List<@NoHTMLTags String> keyUsage;

    private List<@NotBlank @NoHTMLTags String> subjectAlternativeName;

    private Boolean subjectKeyIdentifier;

    private Boolean authorityKeyIdentifier;

}
