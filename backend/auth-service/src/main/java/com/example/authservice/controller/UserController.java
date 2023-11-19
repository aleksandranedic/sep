package com.example.authservice.controller;

import com.example.authservice.api.ResponseOk;
import com.example.authservice.dto.request.UserDetailsRequest;
import com.example.authservice.dto.response.UserDetailResponse;
import com.example.authservice.dto.response.UserInfoResponse;
import com.example.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/pera/{id}")
    public int getUser(@PathVariable long id) {
        System.out.println("TU SMO " + id);
        return (int) (1000 + id);
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserInfoResponse> getLawyers() {
        return userService.getLawyers();
    }

    @GetMapping("/{userEmail}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDetailResponse getLawyer(@PathVariable String userEmail) {
        return userService.getLawyer(userEmail);
    }

    @PutMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public UserDetailResponse editLawyer(@RequestBody UserDetailsRequest user) {
        return userService.editLawyer(user);
    }

    @DeleteMapping("/{userEmail}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseOk deleteUser(@PathVariable String userEmail) {
        userService.deleteUser(userEmail);
        return new ResponseOk("User logically deleted.");
    }
}
