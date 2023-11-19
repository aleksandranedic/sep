package com.example.paymentcardcenter.service;

import com.example.paymentcardcenter.dto.*;

import com.example.paymentcardcenter.model.PaymentResponse;
import com.example.paymentcardcenter.model.RetrofitBank;
import com.example.paymentcardcenter.repository.PaymentResponseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Service
public class PaymentService {
    @Autowired
    PaymentResponseRepo paymentResponseRepo;

    public ResponseEntity<IssuerResponseDTO> proceedToIssuerBank(AcquirerBankDTO acquirerBankDTO) {
        String url = "http://localhost:8000/api/bank/payment/issuer/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitBank retrofitBank = retrofit.create(RetrofitBank.class);

        Call<IssuerResponseDTO> call = retrofitBank.createIssuerPayment(url, acquirerBankDTO);

        try {
            retrofit2.Response<IssuerResponseDTO> response = call.execute();
            if (response.isSuccessful()) {
                System.out.println("uspesno");
                return ResponseEntity.ok().body(response.body());
            } else {
                // Handle unsuccessful response
                return ResponseEntity.status(response.code()).body(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

}
