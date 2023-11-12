package com.example.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class DeviceSuccessfulPairingResponse {
    private UUID deviceId;
}
