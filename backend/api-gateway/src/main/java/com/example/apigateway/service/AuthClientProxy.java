package com.example.apigateway.service;

import com.example.shared.clients.AuthServiceClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthClientProxy extends ClientProxy {

    private final AuthServiceClient authServiceClient;

    private final Logger logger = LoggerFactory.getLogger(AuthClientProxy.class);

    public Optional<Integer> createUser(Long user) {
        try {
            Response<Integer> createUserResponse = execute(() -> authServiceClient.getUser(user));
            return handleResponse(createUserResponse);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return Optional.empty();
    }
}
