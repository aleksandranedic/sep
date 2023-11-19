package com.example.paymentcardcenter.controller;
import com.example.paymentcardcenter.dto.*;
import com.example.paymentcardcenter.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/api/payment")
public class PaymentController {

    @Autowired
    PaymentService paymentService;
    @PostMapping("/issuer/")
    public ResponseEntity<IssuerResponseDTO> proceedToIssuerBank(@RequestBody AcquirerBankDTO acquirerBankDTO) {
        System.out.println(acquirerBankDTO.getAcquirerOrderId());
        return paymentService.proceedToIssuerBank(acquirerBankDTO);
    }


}