package com.example.paypalservice.services;

import com.example.paypalservice.controllers.dto.CreatePaymentDTO;
import com.example.paypalservice.controllers.dto.CreateSubscriptionDTO;
import com.example.paypalservice.controllers.dto.PaymentDTO;
import com.example.paypalservice.model.SubscriptionPlan;
import com.example.paypalservice.repository.PaymentRepository;
import com.example.paypalservice.repository.SubscriptionPlanRepo;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PaypalService {
    @Autowired
    private APIContext apiContext;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private SubscriptionPlanRepo subscriptionPlanRepo;
    public static final String FRONTEND_URL = "http://localhost:4200/";
    public static final String CURRENCY = "USD";
    public static final String INTENT = "sale";
    public static final String DESCRIPTION = "Purchase of agency service";
    public static final String PAYMENT_METHOD = "paypal";


    public String createPayment(CreatePaymentDTO createPaymentDTO) throws IOException {
        double total = createPaymentDTO.getAmount();
        Payee payee = new Payee();
        payee.setEmail("lawagency@gmail.com");
        Amount amount = new Amount();
        amount.setCurrency(CURRENCY);
        total = BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
        amount.setTotal(String.valueOf(total));
        Transaction transaction = new Transaction();
        transaction.setDescription(DESCRIPTION);
        transaction.setAmount(amount);
        transaction.setPayee(payee);

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
        redirectUrls.setReturnUrl(FRONTEND_URL + "dashboard");
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

    public String createSubscription(CreateSubscriptionDTO createSubscriptionDTO) {
        String name = createPlanName(createSubscriptionDTO);
        Optional<SubscriptionPlan> spOpt = subscriptionPlanRepo.findByName(name);
        if (spOpt.isEmpty()) {
            String plan_id = createPlan(name, createSubscriptionDTO);
            subscriptionPlanRepo.save(new SubscriptionPlan(name, plan_id, createSubscriptionDTO.getAmount()));
            return plan_id;
        }
        return spOpt.get().getPlan_id();
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
        System.out.println(paymentDTO);
        try {
            Payment executedPayment = payment.execute(apiContext, paymentExecution);
            if (executedPayment.getState().equals("approved")) {
                onSuccessfulPayment(paymentDTO);
                return "Successful payment";
            } else {
                throw new RuntimeException("Unsuccessful payment");
            }
        } catch (PayPalRESTException e) {
            System.out.println(e);
            throw new RuntimeException("Unsuccessful payment");
        }
    }

    private void onSuccessfulPayment(PaymentDTO paymentDTO) {
        com.example.paypalservice.model.Payment payment = paymentRepository.findByPaypalPaymentId(paymentDTO.getPaymentId());
        payment.setSuccessful(true);
        paymentRepository.save(payment);
    }

    private String createPlanName(CreateSubscriptionDTO createSubscriptionDTO) {
        String name = "";
        if (createSubscriptionDTO.getDigital()) {
            name = name + "digital";
        }
        if (createSubscriptionDTO.getInternet()) {
            name = name + "internet";
        }
        if (createSubscriptionDTO.getCodification()) {
            name = name + "codification";
        }
        if (createSubscriptionDTO.getPrinted()) {
            name = name + "printed";
        }
        if (createSubscriptionDTO.getMonthly()) {
            name = name + "monthly";
        } else {
            name = name + "yearly";
        }
        return name;
    }

    public String createPlan(String name, CreateSubscriptionDTO createSubscriptionDTO) {
        Plan plan = new Plan();
        plan.setName(name);
        plan.setDescription("Basic subscription plan");
        plan.setType("INFINITE"); // Set the plan type (INFINITE or FIXED)
        plan.setPaymentDefinitions(Arrays.asList(
                new PaymentDefinition()
                        .setName("Regular Payment")
                        .setType("REGULAR")
                        .setFrequency("MONTH")
                        .setFrequencyInterval("1")
                        .setAmount(new Currency().setCurrency("USD").setValue(String.valueOf(createSubscriptionDTO.getAmount()))) // Set the amount
        ));
        plan.setMerchantPreferences(new MerchantPreferences()
                .setReturnUrl(FRONTEND_URL + createSubscriptionDTO.getSuccessUrl()) // Set return URL
                .setCancelUrl(FRONTEND_URL + createSubscriptionDTO.getFailedUrl()) // Set cancel URL
                .setAutoBillAmount("YES") // Set automatic billing
                .setInitialFailAmountAction("CONTINUE")
                .setMaxFailAttempts("0")); // Set maximum failed attempts
        plan.setState("ACTIVE");

        try {
            // Create the plan
            Plan createdPlan = plan.create(apiContext);

            if (createdPlan != null) {
//                System.out.println("Plan created successfully");
//                System.out.println(createdPlan);
//                // Activate the plan (if necessary, for 'INFINITE' plans)
//                createdPlan.setState("ACTIVE");
//                List<Patch> patchRequest = new ArrayList<>();
//                Patch patch = new Patch();
//                patch.setPath("/");
//                patch.setValue("{\"state\":\"ACTIVE\"}");
//                patch.setOp("replace");
//                patchRequest.add(patch);
//                createdPlan.update(apiContext, patchRequest);

                System.out.println("Plan activated successfully");
                return createdPlan.getId();
            } else {
                // Handle plan creation error
                System.err.println("Plan creation failed");
                throw new RuntimeException("Error creating new plan");
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating new plan");
        }
    }
}