package com.example.paymentserviceprovider.controller;

import com.example.paymentserviceprovider.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/payment", consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping
    public List<String> getSubscriptions() {
        return List.of();
    }

    @PostMapping
    public void override(@RequestBody List<String> services) {
        System.out.println(services);
    }

    @PostMapping("/{method}")
    public ResponseEntity<Map<String, Object>> proceedPayment(@PathVariable String method, @RequestBody Map<String, Object> req) {
        return paymentService.proceedPayment(method, req);
    }
}