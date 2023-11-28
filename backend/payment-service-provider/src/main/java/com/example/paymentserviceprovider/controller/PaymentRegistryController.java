package com.example.paymentserviceprovider.controller;

import com.example.paymentserviceprovider.service.PaymentRegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "registry/payment")
public class PaymentRegistryController {

    @Autowired
    PaymentRegistryService paymentRegistryService;

    @GetMapping("/refresh")
    public void refreshPaymentServices() {
        paymentRegistryService.loadPaymentRegistry();
    }
}
