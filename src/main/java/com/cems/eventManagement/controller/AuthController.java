package com.cems.eventManagement.controller;

import com.cems.eventManagement.dto.ApiResponse;
import com.cems.eventManagement.dto.LoginRequest;
import com.cems.eventManagement.dto.LoginResponse;
import com.cems.eventManagement.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request){
        LoginResponse tokenData = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(true,"Login Successful", tokenData));
    }
}
