package com.example.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PropertyResponse {
    private UUID id;
    private String name;
    private String address;
    private String image;
    private UserInfoResponse owner;
}
