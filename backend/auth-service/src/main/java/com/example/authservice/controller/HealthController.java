package com.example.authservice.controller;

import com.example.authservice.api.ResponseOk;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseOk health() {
        return new ResponseOk("It works.");
    }
}
