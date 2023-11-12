package com.example.shared.clients;

import com.example.shared.model.order.OrderDto;
import com.example.shared.model.order.SearchOrderDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SearchOrdersClient {

    @POST("order-search/create-order")
    Call<OrderDto> createOrder(@Body SearchOrderDTO searchOrderDTO);
}
