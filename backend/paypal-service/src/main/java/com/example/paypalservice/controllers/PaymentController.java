package com.example.paypalservice.controllers;

import com.example.paypalservice.controllers.dto.CreatePaymentDTO;
import com.example.paypalservice.controllers.dto.CreateSubscriptionDTO;
import com.example.paypalservice.controllers.dto.PaymentDTO;
import com.example.paypalservice.services.PaypalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/payment")
public class PaymentController {

    @Autowired
    PaypalService paypalService;

    @PostMapping("/")
    public ResponseEntity<Map<String,String>> createPayment(@RequestBody CreatePaymentDTO createPaymentDTO) {
        try {
            String response = paypalService.createPayment(createPaymentDTO);
            Map<String, String> jsonResponse = new HashMap<>();
            jsonResponse.put("redirectURL", response);
            return ResponseEntity.ok().body(jsonResponse);
        } catch (RuntimeException | IOException exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        }
    }

    @PostMapping("/subscribe/")
    public ResponseEntity<Map<String,String>> createSubscription(@RequestBody CreateSubscriptionDTO createSubscriptionDTO) {
        try {
            String response = paypalService.createSubscription(createSubscriptionDTO);
            return ResponseEntity.ok().body(Map.of("planId", response));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        }
    }

    @PostMapping(value = "/confirm/")
    public ResponseEntity<Map<String, String>> successPay(@RequestBody PaymentDTO paymentDTO) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", paypalService.executePayment(paymentDTO)));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body((Map.of("message", exception.getMessage())));
        }
    }
}
