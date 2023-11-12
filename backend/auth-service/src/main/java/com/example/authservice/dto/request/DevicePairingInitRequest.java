package com.example.authservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class DevicePairingInitRequest {

    @NotNull
    private UUID propertyId;
}
