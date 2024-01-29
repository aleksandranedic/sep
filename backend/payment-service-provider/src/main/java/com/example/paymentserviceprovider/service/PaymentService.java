package com.example.paymentserviceprovider.service;

import com.example.paymentserviceprovider.logger.Logger;
import com.example.paymentserviceprovider.model.paymentRegistry.AuthCall;
import com.example.paymentserviceprovider.model.paymentRegistry.RetrofitPayment;
import com.example.paymentserviceprovider.model.paymentRegistry.Transaction;
import com.example.paymentserviceprovider.repository.TransactionRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentService {

    @Autowired
    TransactionRepository transactionRepo;
    @Autowired
    Logger logger;

    public static final String API_URL = "http://localhost:8081";

    public ResponseEntity<Map<String, Object>> proceedPayment(@RequestBody Map<String, Object> req, HttpServletRequest httpRequest) {
        try {
            authenticate(httpRequest);
        } catch (Exception e) {
            logger.error("Authentication failed. Message: " + e.getMessage(), new Date().toString(), "PaymentServiceProvider", req);
            return ResponseEntity.status(500).body(Map.of("Error", "Failed to make the request"));
        }

        String url = API_URL + req.get("path") + "/";//PaymentServiceRegistry.getDescriptor(method).url();
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        System.out.println(url);
// Set the desired timeout values (in seconds)
        httpClientBuilder.connectTimeout(80, TimeUnit.SECONDS); // Set connection timeout
        httpClientBuilder.readTimeout(80, TimeUnit.SECONDS); // Set read timeout
        httpClientBuilder.writeTimeout(80, TimeUnit.SECONDS); // Set write timeout

// Build OkHttpClient
        OkHttpClient httpClient = httpClientBuilder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        RetrofitPayment retrofitPayment = retrofit.create(RetrofitPayment.class);

        Map<String, String> merchantInfo = createAndSaveMerchantTransaction(req);
        req.putAll(merchantInfo);
        req.put("successUrl", "payment/success");
        req.put("failedUrl", "payment/failed");
        req.put("errorUrl", "payment/error");

        Call<Map<String, Object>> call = retrofitPayment.forwardPayment(url, req);

        try {
            System.out.println("Before execute");
            Response<Map<String, Object>> response = call.execute();
            System.out.println("After execute");
            if (response.isSuccessful()) {
                logger.info("Payment successful", new Date().toString(), "PaymentServiceProvider", req);
                System.out.println(url);
                System.out.println("sucess");
                System.out.println(response);
                System.out.println(response.body());
                return ResponseEntity.ok(response.body());
            } else {
                // Handle unsuccessful response
                logger.error("Payment failed. Message: " + response.message(), new Date().toString(), "PaymentServiceProvider", req);
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
        reqBody.forEach((key, value) -> merchantInfo.put(key, value.toString()));
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

    private void authenticate(HttpServletRequest httpRequest) throws IOException {
        Cookie[] cookies = httpRequest.getCookies();
        Cookie accessCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("access_token"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No access token found"));
        String token = accessCookie.getValue();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request originalRequest = chain.request();
                    Request.Builder builder = originalRequest.newBuilder()
                            .header("Authorization", "Bearer " + token);
                    Request newRequest = builder.build();
                    return chain.proceed(newRequest);
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8001/auth/verify/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        AuthCall auth = retrofit.create(AuthCall.class);
        Call<Map<String, Object>> call = auth.authenticate("http://localhost:8001/auth/verify/");
        Response<Map<String, Object>> response = call.execute();
        if (!response.isSuccessful()) {
           throw new RuntimeException("Authentication failed");
        }
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
