package com.example.authservice.dto.request;

import com.example.authservice.validation.NoHTMLTags;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CSRRejectionRequest {

    @NotBlank
    @NoHTMLTags
    private String rejectionReason;
}
