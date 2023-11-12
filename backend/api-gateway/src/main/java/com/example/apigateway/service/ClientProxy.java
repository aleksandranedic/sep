package com.example.apigateway.service;

import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class ClientProxy {

    protected <T> Optional<T> handleResponse(Response<T> response) {
        if (response.isSuccessful()) {
            return Optional.of(response.body());
        }

        return Optional.empty();
    }

    protected <T> T handleResponseRaw(Response<T> response) {
        if (response.isSuccessful()) {
            return response.body();
        }

        return null;
    }


    protected <T> Response<T> execute(Supplier<Call<T>> callSupplier) throws IOException {
        return callSupplier.get().execute();
    }
}
