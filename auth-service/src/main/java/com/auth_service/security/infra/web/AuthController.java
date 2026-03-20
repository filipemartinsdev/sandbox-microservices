package com.auth_service.security.infra.web;

import com.auth_service.common.dto.StandardResponse;
import com.auth_service.security.application.dto.LoginRequest;
import com.auth_service.security.application.dto.LoginResponse;
import com.auth_service.security.application.dto.RefreshRequest;
import com.auth_service.security.application.dto.RegisterRequest;
import com.auth_service.security.application.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<StandardResponse<Void>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(StandardResponse.success());
    }

    @PostMapping("/login")
    public ResponseEntity<StandardResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                StandardResponse.success(
                        authService.login(request)
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<StandardResponse<LoginResponse>> register(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(
                StandardResponse.success(
                        authService.refresh(request)
                )
        );
    }
}
