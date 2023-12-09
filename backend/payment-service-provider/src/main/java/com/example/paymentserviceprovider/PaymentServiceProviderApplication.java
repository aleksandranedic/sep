package com.example.paymentserviceprovider;

import com.example.paymentserviceprovider.service.PaymentRegistryService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentServiceProviderApplication {

    @Autowired
    PaymentRegistryService paymentRegistryService;
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceProviderApplication.class, args);
    }

    @PostConstruct
    private void registerPaymentServices() {
        paymentRegistryService.loadPaymentRegistry();
    }
}
