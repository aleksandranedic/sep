package com.example.paymentcardcenter.model;

import com.example.paymentcardcenter.dto.AcquirerBankDTO;
import com.example.paymentcardcenter.dto.IssuerResponseDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;


public interface RetrofitBank {
    @POST
    Call<IssuerResponseDTO> createIssuerPayment(@Url String url, @Body AcquirerBankDTO req);
}
