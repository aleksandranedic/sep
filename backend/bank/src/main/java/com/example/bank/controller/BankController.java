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
    public ResponseEntity<?> pay(@PathVariable String method, @RequestBody PSPPaymentDTO pspPaymentDTO) {
        return bankService.createFullPayment(method, pspPaymentDTO);
    }

    @PostMapping("/card/payment")
    public ResponseEntity<?> payWithCardQR(@RequestBody CardPaymentDTO cardPaymentDTO) {
        return bankService.payWithCard(cardPaymentDTO);
    }

    @PostMapping("/card/edit")
    public boolean editCardData(@RequestBody CardPaymentDTO cardPaymentDTO) {
        return bankService.editCardData(cardPaymentDTO);
    }

    @PostMapping("/qr/payment")
    public ResponseEntity<?> payWithCardQR(@RequestBody QRPaymentDTO qrCode) {
        return bankService.payWithQr(qrCode);
    }

    @PostMapping("/payment/issuer/")
    public ResponseEntity<?> issuerPay(@RequestBody PCCPayloadDTO pccPayloadDTO) { //PCCResponseDTO
        System.out.println(pccPayloadDTO);
        return bankService.issuerPay(pccPayloadDTO);
    }

    @GetMapping("/add")
    public void addUser() {
        User user = new User("lala", 10000, "1", "Agencija za izdavanje zakona");
        userRepo.save(user);

        CardInfo ci = new CardInfo("0000111122223333", "460", "Smilja", 11, 2024);
        User buyer = new User("lele", 5000, ci, "1", "Smilja Smiljic");
        buyer.setId("e3661c31-d1a4-47ab-94b6-1c6500dccf24");
        userRepo.save(buyer);
    }
}
