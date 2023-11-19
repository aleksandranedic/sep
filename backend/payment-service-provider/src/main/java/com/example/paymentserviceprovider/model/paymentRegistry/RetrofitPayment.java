package com.example.paymentserviceprovider.model.paymentRegistry;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

import java.util.Map;

public interface RetrofitPayment {
    @POST
    Call<Map<String, Object>> forwardPayment(@Url String url, @Body Map<String, Object> req);
}
