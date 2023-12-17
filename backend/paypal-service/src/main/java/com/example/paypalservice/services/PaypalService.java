package com.example.paypalservice.services;

import com.example.paypalservice.controllers.dto.CreatePaymentDTO;
import com.example.paypalservice.controllers.dto.PaymentDTO;
import com.example.paypalservice.repository.PaymentRepository;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaypalService {
    @Autowired
    private APIContext apiContext;
    @Autowired
    private PaymentRepository paymentRepository;
    public static final String FRONTEND_URL = "http://localhost:4200/";
    public static final String CURRENCY = "USD";
    public static final String INTENT = "sale";
    public static final String DESCRIPTION = "Purchase of agency service";
    public static final String PAYMENT_METHOD = "paypal";


    public String createPayment(CreatePaymentDTO createPaymentDTO) throws IOException {
        double total = createPaymentDTO.getAmount();
        Amount amount = new Amount();
        amount.setCurrency(CURRENCY);
        total = BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
        amount.setTotal(String.valueOf(total));
        Transaction transaction = new Transaction();
        transaction.setDescription(DESCRIPTION);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(PAYMENT_METHOD);

        Payment payment = new Payment();
        payment.setIntent(INTENT);
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(FRONTEND_URL + createPaymentDTO.getFailedUrl());
        redirectUrls.setReturnUrl(FRONTEND_URL + createPaymentDTO.getSuccessUrl());
        payment.setRedirectUrls(redirectUrls);
        try {
            Payment createdPayment = payment.create(apiContext);
            for (Links link : createdPayment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    storePaymentInfo(total, createPaymentDTO.getMerchantOrderId(), createdPayment.getId());
                    return link.getHref();
                }
            }
        } catch (PayPalRESTException e) {
            throw new RuntimeException("Unsuccessful creation of payment");
        }
        return null;
    }

    private void storePaymentInfo(double total, String merchantOrderId, String paypalPaymentId) {
        com.example.paypalservice.model.Payment payment = new com.example.paypalservice.model.Payment();
        payment.setAmount(total);
        payment.setSuccessful(false);
        payment.setMerchantOrderId(merchantOrderId);
        payment.setPaypalPaymentId(paypalPaymentId);
        payment.setTransactionDate(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    public String executePayment(PaymentDTO paymentDTO) {
        Payment payment = new Payment();
        payment.setId(paymentDTO.getPaymentId());
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(paymentDTO.getPayerId());
        try {
            Payment executedPayment = payment.execute(apiContext, paymentExecution);
            if (executedPayment.getState().equals("approved")) {
                onSuccessfulPayment(paymentDTO);
                return "Successful payment";
            } else {
                throw new RuntimeException("Unsuccessful payment");
            }
        } catch (PayPalRESTException e) {
            throw new RuntimeException("Unsuccessful payment");
        }
    }

    private void onSuccessfulPayment(PaymentDTO paymentDTO) {
        com.example.paypalservice.model.Payment payment = paymentRepository.findByPaypalPaymentId(paymentDTO.getPaymentId());
        payment.setSuccessful(true);
        paymentRepository.save(payment);
        transferMoneyToMerchant(payment);
    }

    private void transferMoneyToMerchant(com.example.paypalservice.model.Payment payment) {
        
    }
}
