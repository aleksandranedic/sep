package com.example.authservice.controller;

import com.example.authservice.api.ResponseOk;
import com.example.authservice.dto.request.BlacklistTokenRequest;
import com.example.authservice.dto.response.BlacklistedTokenInfo;
import com.example.authservice.security.TokenProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/tokens")
@PreAuthorize("hasRole('ADMIN')")
public class TokenBlacklistController {

    @Autowired
    private TokenProvider tokenProvider;

    @PostMapping
    public ResponseOk add(@RequestBody @Valid BlacklistTokenRequest request) {
        tokenProvider.addTokenToBlacklist(request.getToken());
        return new ResponseOk("Token blacklisted.");
    }

    @GetMapping
    public List<BlacklistedTokenInfo> view() {
        return tokenProvider.getTokenBlacklistInfo();
    }
}
