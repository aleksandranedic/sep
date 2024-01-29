package com.example.paymentserviceprovider.model.paymentRegistry;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

import java.util.Map;

public interface AuthCall {

    @GET
    Call<Map<String, Object>> authenticate(@Url String url);
}
