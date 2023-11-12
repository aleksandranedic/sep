package com.example.authservice.dto.request;

import com.example.authservice.validation.NoHTMLTags;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CertificateRevocationRequest {
    @Size(max = 256)
    @NoHTMLTags
    private String reason;
}
