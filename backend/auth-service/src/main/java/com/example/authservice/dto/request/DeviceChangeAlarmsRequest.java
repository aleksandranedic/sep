package com.example.authservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeviceChangeAlarmsRequest {
    @NotNull
    private String[][] alarms;
}
