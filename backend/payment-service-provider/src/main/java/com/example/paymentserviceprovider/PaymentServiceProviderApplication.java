package com.example.paymentserviceprovider;

import com.example.paymentserviceprovider.model.paymentRegistry.PaymentServiceRegistry;
import com.example.paymentserviceprovider.service.PaymentRegistryService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

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
