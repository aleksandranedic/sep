package com.example.paypalservice.controllers;

import com.example.paypalservice.controllers.dto.PaymentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/payment")
public class PaymentController {

    @PostMapping("/")
    public ResponseEntity<PaymentDTO> pay(@RequestBody PaymentDTO paymentDTO) {
        return ResponseEntity.ok(paymentDTO);
    }
}