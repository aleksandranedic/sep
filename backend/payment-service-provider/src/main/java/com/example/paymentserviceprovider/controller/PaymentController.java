package com.example.paymentserviceprovider.controller;

import com.example.paymentserviceprovider.dto.UPPPayload;
import com.example.paymentserviceprovider.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
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

    @PostMapping("/refund")
    public ResponseEntity<Map<String,String>> refund(@RequestBody UPPPayload payload) {
        System.out.println(payload);
        return ResponseEntity.ok(Map.of("message", "Refunded successfully"));
    }


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

    @PostMapping("/proceed")
    public ResponseEntity<Map<String, Object>> proceedPayment(@RequestBody Map<String, Object> req, HttpServletRequest httpRequest) {
        System.out.println(req);
        return paymentService.proceedPayment(req, httpRequest);
    }
}