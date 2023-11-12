package com.example.shared.clients;

import com.example.shared.model.restaurant.CreateMenuItemRequest;
import com.example.shared.model.restaurant.CreateRestaurantRequest;
import com.example.shared.model.restaurant.MenuItemDto;
import com.example.shared.model.restaurant.RestaurantDto;
import com.example.shared.model.user.CreateUserRequest;
import com.example.shared.model.user.NotificationDto;
import com.example.shared.model.user.UserDto;
import org.springframework.web.bind.annotation.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface BackofficeClient {

    @POST("back-office/user")
    Call<UserDto> createUser(@Body CreateUserRequest createUserRequest);

    @GET("back-office/user/{id}")
    Call<UserDto> getUser(@Path("id") Long userId);

    @POST("back-office/restaurant")
    Call<RestaurantDto> createRestaurant(@Body CreateRestaurantRequest createRestaurantRequest);

    @POST("back-office/notification")
    Call<NotificationDto> createNotification(@Body String notificationDto);

    @POST("back-office/restaurant/{id}/menu-item")
    Call<MenuItemDto> createMenuItem(@Path("id") Long restaurantId, @Body CreateMenuItemRequest createMenuItemRequest);

    @GET("back-office/restaurant/{id}")
    Call<RestaurantDto> getRestaurant(@Path("id") Long restaurantId);

    @GET("back-office/restaurants")
    Call<List<RestaurantDto>> getRestaurants();

    @GET("back-office/notifications")
    Call<List<NotificationDto>> getNotifications();

    @GET("back-office/restaurant/{id}/menu-items")
    Call<List<MenuItemDto>> getMenuItems(@Path("id") Long restaurantId, @Body List<Long> menuItemIds);

    @GET("back-office/restaurant/{id}/menu-items")
    Call<List<MenuItemDto>> getMenuItems(@Path("id") Long restaurantId);

    @POST("back-office/restaurant/{id}/remove-menu-item")
    Call<RestaurantDto> removeMenuItem(@Path("id") Long restaurantId, @Body String name);

}
