package com.example.paymentcryptoservice.controllers;

import com.example.paymentcryptoservice.dto.ConfirmPaymentDTO;
import com.example.paymentcryptoservice.dto.PaymentDTO;
import com.example.paymentcryptoservice.dto.TransactionResponseDTO;
import com.example.paymentcryptoservice.model.TransactionStatus;
import com.example.paymentcryptoservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/")
    public ResponseEntity<TransactionResponseDTO> pay(@RequestBody PaymentDTO paymentDTO) throws IOException {
        String email = paymentDTO.getEmail();
        Double amount = paymentDTO.getAmount();
        TransactionResponseDTO responseDTO = paymentService.generateAddress(email, amount);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/status/")
    public ResponseEntity<Map<String, String>> check(@RequestBody ConfirmPaymentDTO confirmPaymentDTO) {
        UUID id = UUID.fromString(confirmPaymentDTO.getTransactionId());
        return ResponseEntity.ok().body(Map.of("status", paymentService.check(id).name()));
    }

}