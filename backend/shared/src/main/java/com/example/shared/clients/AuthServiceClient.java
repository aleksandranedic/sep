package com.example.shared.clients;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AuthServiceClient {

    @GET("auth-service/user/{id}")
    Call<Integer> getUser(@Path("id") Long userId);
}
