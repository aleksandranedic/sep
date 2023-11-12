package com.example.apigateway.service;

import com.example.shared.clients.BackofficeClient;
import com.example.shared.model.restaurant.CreateMenuItemRequest;
import com.example.shared.model.restaurant.CreateRestaurantRequest;
import com.example.shared.model.restaurant.MenuItemDto;
import com.example.shared.model.restaurant.RestaurantDto;
import com.example.shared.model.user.CreateUserRequest;
import com.example.shared.model.user.NotificationDto;
import com.example.shared.model.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BackOfficeClientProxy extends ClientProxy {

    private final BackofficeClient backofficeClient;

    private final Logger logger = LoggerFactory.getLogger(BackOfficeClientProxy.class);

    public Optional<UserDto> createUser(CreateUserRequest user) {
        try {
            Response<UserDto> createUserResponse = execute(() -> backofficeClient.createUser(user));
            return handleResponse(createUserResponse);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return Optional.empty();
    }

    public Optional<UserDto> getUser(Long userId) {
        try {
            Response<UserDto> getUserResponse = execute(() -> backofficeClient.getUser(userId));
            return handleResponse(getUserResponse);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return Optional.empty();
    }

    public Optional<RestaurantDto> createRestaurant(CreateRestaurantRequest createRestaurantRequest) {
        try {
            Response<RestaurantDto> createRestaurantResponse = execute(() -> backofficeClient.createRestaurant(createRestaurantRequest));
            return handleResponse(createRestaurantResponse);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return Optional.empty();
    }

    public Optional<RestaurantDto> getRestaurant(Long restaurantId) {
        try {
            Response<RestaurantDto> getRestaurantResponse = execute(() -> backofficeClient.getRestaurant(restaurantId));
            return handleResponse(getRestaurantResponse);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return Optional.empty();
    }

    public List<MenuItemDto> getMenuItems(Long restaurantId, List<Long> menuItemIds) {
        try {
            Response<List<MenuItemDto>> getMenuItems = execute(() -> backofficeClient.getMenuItems(restaurantId, menuItemIds));
            return getMenuItems.body();

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return Collections.emptyList();
    }

    public List<MenuItemDto> getMenuItems(Long restaurantId) {
        try {
            Response<List<MenuItemDto>> getMenuItems = execute(() -> backofficeClient.getMenuItems(restaurantId));
            return getMenuItems.body();

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return Collections.emptyList();
    }

    public Optional<MenuItemDto> createMenuItem(Long restaurantId, CreateMenuItemRequest createMenuItemRequest) {
        try {
            Response<MenuItemDto> createMenuItemResponse = execute(() -> backofficeClient.createMenuItem(restaurantId, createMenuItemRequest));
            return handleResponse(createMenuItemResponse);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return Optional.empty();
    }

    public List<RestaurantDto> getRestaurants() {
        try {
            Response<List<RestaurantDto>> response = execute(backofficeClient::getRestaurants);
            return response.body();

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Optional<RestaurantDto> removeMenuItem(Long restaurantId, String name) {
        try {
            Response<RestaurantDto> response = execute(() -> backofficeClient.removeMenuItem(restaurantId, name));
            return Optional.ofNullable(response.body());

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<NotificationDto> getNotifications() {
        try {
            Response<List<NotificationDto>> response = execute(backofficeClient::getNotifications);
            return response.body();

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Optional<NotificationDto> createNotification(String notificationDto) {
        try {
            Response<NotificationDto> createNotificationResponse = execute(() -> backofficeClient.createNotification(notificationDto));
            return handleResponse(createNotificationResponse);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return Optional.empty();
    }
}
