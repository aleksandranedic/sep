package com.example.bank.model;

import com.example.bank.dto.PCCPayloadDTO;
import com.example.bank.dto.PCCResponseDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;


public interface RetrofitPCC {
    @POST
    Call<PCCResponseDTO> getIssuerBankResult(@Url String url, @Body PCCPayloadDTO req);
}