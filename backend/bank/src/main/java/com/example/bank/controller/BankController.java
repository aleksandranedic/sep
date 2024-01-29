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

    @PostMapping("/pay/card/")
    public ResponseEntity<?> pay( @RequestBody PSPPaymentDTO pspPaymentDTO) {
        System.out.println("ALOOOO USPEO SAM BANK CONTROLLER");
        return bankService.createFullPayment("card", pspPaymentDTO);
    }

    @PostMapping("/pay/qr/")
    public ResponseEntity<?> payQR( @RequestBody PSPPaymentDTO pspPaymentDTO) {
        return bankService.createFullPayment("qr", pspPaymentDTO);
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
        user.setId("658a0841682d9a1e141599f8");
        userRepo.save(user);

        CardInfo ci = new CardInfo();
        ci.setSecurityCode("0000111122223333");
        ci.setPan("460");
        ci.setCardHolderName("Smilja");
        ci.setExpiryYear(24);
        ci.setExpiryMonth(11);

        User buyer = new User("lele", 5000, ci, "1", "Smilja Smiljic");
        buyer.setId("e3661c31-d1a4-47ab-94b6-1c6500dccf24");
        userRepo.save(buyer);
    }
}
