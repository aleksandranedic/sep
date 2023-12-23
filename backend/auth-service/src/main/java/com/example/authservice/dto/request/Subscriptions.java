package com.example.authservice.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class Subscriptions {
    List<String> services;
    String userId;
}
