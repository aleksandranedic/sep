package com.example.paymentcardcenter.service;

import com.example.paymentcardcenter.dto.*;

import com.example.paymentcardcenter.logger.Logger;
import com.example.paymentcardcenter.model.PCCTransaction;
import com.example.paymentcardcenter.model.RetrofitBank;
import com.example.paymentcardcenter.repository.PCCTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Service
public class PaymentService {
    @Autowired
    PCCTransactionRepo pccTransactionRepo;
    @Autowired
    Logger logger;

    public ResponseEntity<?> proceedToIssuerBank(AcquirerBankDTO acquirerBankDTO) {
        logger.info("Proceed transaction to issuer bank", new Date().toString(), "PaymentCardService", acquirerBankDTO);
        String url = "http://localhost:8000/api/bank/payment/issuer/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitBank retrofitBank = retrofit.create(RetrofitBank.class);

        Call<?> call = retrofitBank.createIssuerPayment(url, acquirerBankDTO);

        try {
            retrofit2.Response<?> response = call.execute();
            System.out.println("Response od issuer");
            System.out.println(response);
            if (response.isSuccessful()) {
                logger.info("Issuer bank response successfully", new Date().toString(), "PaymentCardService", response.body());
                IssuerResponseDTO issuerResponseDTO = (IssuerResponseDTO) response.body();
                if (issuerResponseDTO != null) {
                    pccTransactionRepo.save(new PCCTransaction(acquirerBankDTO.getCardInfo(), acquirerBankDTO.getAcquirerOrderId(), acquirerBankDTO.getAcquirerTimestamp(), issuerResponseDTO.getIssuerOrderId(), issuerResponseDTO.getIssuerTimestamp()));
                    logger.info("Save transaction to database", new Date().toString(), "PaymentCardService", issuerResponseDTO);
                }
                return ResponseEntity.ok().body(issuerResponseDTO);
            } else {
                // Handle unsuccessful response
                logger.error("Issuer bank response unsuccessfully", new Date().toString(), "PaymentCardService", response.body());
                return ResponseEntity.status(response.code()).body(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Issuer bank response unsuccessfully", new Date().toString(), "PaymentCardService", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

}
