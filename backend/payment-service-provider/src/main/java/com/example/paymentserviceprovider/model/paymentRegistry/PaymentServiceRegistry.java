package com.example.paymentserviceprovider.model.paymentRegistry;

import java.util.HashMap;
import java.util.Map;

public class PaymentServiceRegistry {
    public record PaymentDescriptor(String url) {}
    private static final Map<String, PaymentDescriptor> registeredPaymentServices = new HashMap<>();

    public static void registerNewPaymentService(String method, PaymentDescriptor paymentDescriptor) {
        registeredPaymentServices.put(method, paymentDescriptor);
    }

    public static PaymentDescriptor getDescriptor(String method) {
        return registeredPaymentServices.get(method);
    }
}
