package com.example.paymentserviceprovider;

import com.example.paymentserviceprovider.model.paymentRegistry.PaymentServiceRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
public class PaymentServiceProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceProviderApplication.class, args);
    }

    @PostConstruct
    private static void registerPaymentServices() {
        try (BufferedReader br = new BufferedReader(new FileReader("paymentRegistry.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                System.out.println(Arrays.toString(values));
                PaymentServiceRegistry.registerNewPaymentService(values[0], new PaymentServiceRegistry.PaymentDescriptor(values[1]));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
    }
}
