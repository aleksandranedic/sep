package com.example.paymentserviceprovider;

import com.example.paymentserviceprovider.model.paymentRegistry.PaymentServiceRegistry;
import jakarta.annotation.PostConstruct;
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

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceProviderApplication.class, args);
    }

    @PostConstruct
    private static void registerPaymentServices() {
        String filePath = "paymentRegistry.csv";
        Resource resource = new ClassPathResource(filePath);

        if (resource.exists()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] values = line.split(",");
                    System.out.println(Arrays.toString(values));
                    PaymentServiceRegistry.registerNewPaymentService(values[0], new PaymentServiceRegistry.PaymentDescriptor(values[1]));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("File not found: " + filePath);
        }
    }
}
