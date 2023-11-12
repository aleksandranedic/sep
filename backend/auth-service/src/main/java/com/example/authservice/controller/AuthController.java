package com.example.authservice.controller;

import com.example.authservice.api.ResponseOk;
import com.example.authservice.dto.request.*;
import com.example.authservice.dto.response.LoginResponse;
import com.example.authservice.dto.response.UserInfoResponse;
import com.example.authservice.model.User;
import com.example.authservice.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) throws Exception {
        return accountService.login(loginRequest, response);
    }

    @PostMapping("/logout")
    public ResponseOk logout(HttpServletRequest request, HttpServletResponse response) {
        accountService.logout(request, response);
        return new ResponseOk("Success");
    }

    @PostMapping("/register")
    public ResponseOk register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        accountService.registerLawyer(registrationRequest);
        return new ResponseOk("User registered successfully.");
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseOk create(@Valid @RequestBody UserDetailsRequest createUserRequest) {
        accountService.createLawyer(createUserRequest);
        return new ResponseOk("User created successfully.");
    }

    @PostMapping("/register/verify")
    public ResponseOk verify(@Valid @RequestBody VerificationRequest verificationRequest) {
        accountService.verifyEmail(verificationRequest);
        return new ResponseOk("Email verified successfully.");
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public UserInfoResponse getLoggedUserInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return new UserInfoResponse(user);
    }

    @GetMapping("/is-password-set/{verificationCode}")
    public boolean isPasswordSet(@PathVariable String verificationCode) {
        return accountService.isPasswordSet(verificationCode);
    }

    @PutMapping("/set-password")
    public ResponseOk setPassword(@RequestBody SetPasswordRequest setPasswordRequest) {
        accountService.setPassword(setPasswordRequest);
        return new ResponseOk("Password set successfully. User is verified.");
    }
}
