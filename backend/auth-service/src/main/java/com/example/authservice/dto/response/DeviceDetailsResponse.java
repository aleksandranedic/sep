package com.example.authservice.dto.response;

import com.example.authservice.dto.request.DeviceSensorInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class DeviceDetailsResponse {
    private UUID id;
    private String name;
    private String type;
    private String manufacturer;
    private String macAddress;
    private String label;
    private PropertyResponse property;
    private List<DeviceSensorInfo> sensors;
    private String[][] alarms;
}
