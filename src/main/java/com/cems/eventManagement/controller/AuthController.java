package com.cems.eventManagement.controller;

import com.cems.eventManagement.dto.ApiResponse;
import com.cems.eventManagement.dto.LoginRequest;
import com.cems.eventManagement.dto.LoginResponse;
import com.cems.eventManagement.security.RateLimitingService;
import com.cems.eventManagement.services.AuthService;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private RateLimitingService rateLimitingService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request){
        Bucket bucket = rateLimitingService.resolveBucket(request.getEmail());

        if(!bucket.tryConsume(1)){
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new ApiResponse<>(false, "Too many login attempts. Please try again after 1 minute.", null));
        }
        LoginResponse tokenData = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(true,"Login Successful", tokenData));
    }
}
