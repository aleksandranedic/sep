package com.example.paymentcardservice.controllers;

import com.example.paymentcardservice.dto.PaymentDTO;
import org.springframework.beans.factory.annotation.Autowired;
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

