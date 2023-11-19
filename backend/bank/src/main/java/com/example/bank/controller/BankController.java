package com.example.bank.controller;

import com.example.bank.dto.*;
import com.example.bank.model.CardInfo;
import com.example.bank.model.User;
import com.example.bank.repository.UserRepo;
import com.example.bank.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/bank")
public class BankController {

    @Autowired
    BankService bankService;
    @Autowired
    UserRepo userRepo;

    @PostMapping("/pay/{method}/")
    public ResponseEntity<PaymentResponseDTO> pay(@PathVariable String method, @RequestBody PSPPaymentDTO pspPaymentDTO) {
        return bankService.createFullPayment(method, pspPaymentDTO);
    }

    @PostMapping("/{method}/payment")
    public ResponseEntity<CardPaymentResponseDTO> payWithCardQR(@PathVariable String method, @RequestBody CardPaymentDTO cardPaymentDTO) {
        return bankService.payWithCard(method, cardPaymentDTO);
    }


    @PostMapping("/payment/issuer/")
    public ResponseEntity<PCCResponseDTO> issuerPay(@RequestBody PCCPayloadDTO pccPayloadDTO) {
        return bankService.issuerPay(pccPayloadDTO);
    }

    @GetMapping("/add")
    public void addUser() {
        User user = new User("lala", 10000, "1", "Agencija za izdavanje zakona");
        userRepo.save(user);

        CardInfo ci = new CardInfo("1239459495", "460", "Smilja", 11, 2023);
        User buyer = new User("lele", 5000, ci, "1", "Smilja Smiljic");
        userRepo.save(buyer);
    }
}
