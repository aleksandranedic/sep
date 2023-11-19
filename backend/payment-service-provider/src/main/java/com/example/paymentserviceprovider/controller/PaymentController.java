package com.example.paymentserviceprovider.controller;

import com.example.paymentserviceprovider.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping(path = "/api/payment")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribeToMethod(@RequestBody String method) {
        return ResponseEntity.ok(method);
    }

    @PostMapping("/remove")
    public ResponseEntity<String> subscribeToRemove(String method) {
        return ResponseEntity.ok(method);
    }

    @PostMapping("/{method}")
    public ResponseEntity<Map<String, Object>> proceedPayment(@PathVariable String method, @RequestBody Map<String, Object> req) {
        return paymentService.proceedPayment(method, req);
    }
}