package com.example.paymentserviceprovider.service;

import com.example.paymentserviceprovider.model.paymentRegistry.PaymentRegistry;
import com.example.paymentserviceprovider.model.paymentRegistry.PaymentServiceRegistry;
import com.example.paymentserviceprovider.repository.PaymentRegistryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentRegistryService {

    @Autowired
    PaymentRegistryRepo paymentRegistryRepo;

    public void loadPaymentRegistry() {
        List<PaymentRegistry> registries = paymentRegistryRepo.findAll();
        for (PaymentRegistry pr : registries) {
            PaymentServiceRegistry.registerNewPaymentService(pr.getKey(), new PaymentServiceRegistry.PaymentDescriptor(pr.getUrl()));
        }
    }
}
