package com.example.shared.clients;

import com.example.shared.model.order.OrderDto;
import com.example.shared.model.order.OrderStatus;
import com.example.shared.model.order.OrderStatusUpdateRequest;
import com.example.shared.model.order.ProcessOrderRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OrderProcessingClient {

    @POST("order-processing/order")
    Call<OrderDto> processOrder(@Body ProcessOrderRequest processOrderRequest);

    @GET("order-processing/order/{id}")
    Call<OrderDto> getOrder(@Path("id") String id);

    @GET("order-processing/order")
    Call<OrderDto> getOrders();

    @PUT("order-processing/order/{id}/status")
    Call<OrderStatus> updateOrderStatus(@Path("id") String id, @Body OrderStatusUpdateRequest orderStatusUpdateRequest);
}
