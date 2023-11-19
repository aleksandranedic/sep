package com.example.paymentserviceprovider.service;

import com.example.paymentserviceprovider.model.paymentRegistry.PaymentServiceRegistry;
import com.example.paymentserviceprovider.model.paymentRegistry.RetrofitPayment;
import com.example.paymentserviceprovider.model.paymentRegistry.Transaction;
import com.example.paymentserviceprovider.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;


@Service
public class PaymentService {

    @Autowired
    TransactionRepository transactionRepo;

    public ResponseEntity<Map<String, Object>> proceedPayment(@PathVariable String method, @RequestBody Map<String, Object> req) {
        String url = PaymentServiceRegistry.getDescriptor(method).url();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitPayment retrofitPayment = retrofit.create(RetrofitPayment.class);

        Map<String, String> merchantInfo = createAndSaveMerchantTransaction(req);
        req.putAll(merchantInfo);
        req.put("successUrl", "http://localhost:4200/payment/success");
        req.put("failedUrl", "http://localhost:4200/payment/failed");
        req.put("errorUrl", "http://localhost:4200/payment/error");

        Call<Map<String, Object>> call = retrofitPayment.forwardPayment(url, req);

        try {
            Response<Map<String, Object>> response = call.execute();
            if (response.isSuccessful()) {
                return ResponseEntity.ok(response.body());
            } else {
                // Handle unsuccessful response
                System.out.println(url);
                return ResponseEntity.status(response.code()).body(Map.of("Error", response.message()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("Error", "Failed to make the request"));
        }
    }

    private Map<String, String> createAndSaveMerchantTransaction(Map<String, Object> reqBody) {
        Map<String, String> merchantInfo = new HashMap<>();
        String merchantOrderId = createMerchantOrderId();
        String merchantOrderTimestamp = LocalDateTime.now().toString();
        merchantInfo.put("merchantOrderId", merchantOrderId);
        merchantInfo.put("merchantTimestamp", merchantOrderTimestamp);
        saveTransaction(merchantOrderId, merchantOrderTimestamp, reqBody);
        return merchantInfo;
    }

    private void saveTransaction(String merchantOrderId, String merchantTimestamp, Map<String, Object> reqBody) {
        Map<String, Object> filteredData = removePasswords(reqBody);
        Transaction transaction = new Transaction(merchantOrderId, merchantTimestamp, filteredData);
        transactionRepo.save(transaction);
    }

    private Map<String, Object> removePasswords(Map<String, Object> data) {
        Map<String, Object> filteredData = new HashMap<>();
        data.forEach((key, value) -> {
            if (!key.toLowerCase().contains("password")) {
                filteredData.put(key, value);
            }
        });
        return filteredData;
    }

    private String createMerchantOrderId() {
        Random random = new Random();
        String merchantOrderId;
        boolean uniqueIdFound = false;
        do {
            merchantOrderId = String.format("%010d", random.nextInt(1_000_000_000));
            Optional<Transaction> transactionOptional = transactionRepo.findByMerchantOrderId(merchantOrderId);
            if (transactionOptional.isEmpty()) {
                uniqueIdFound = true;
            }
        } while (!uniqueIdFound);
        return merchantOrderId;
    }
}
