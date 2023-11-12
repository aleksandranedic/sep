package com.example.apigateway.controller;

import com.example.apigateway.service.BackOfficeClientProxy;
import com.example.shared.model.user.CreateUserRequest;
import com.example.shared.model.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("api/back-office/")
@RequiredArgsConstructor
public class BackOfficeUserController {

    private final BackOfficeClientProxy backOfficeClientProxy;

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<UserDto> createUser(CreateUserRequest user) {
        Optional<UserDto> newUser = backOfficeClientProxy.createUser(user);
        return newUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        Optional<UserDto> user = backOfficeClientProxy.getUser(userId);
        return ResponseEntity.of(user);
    }
}
