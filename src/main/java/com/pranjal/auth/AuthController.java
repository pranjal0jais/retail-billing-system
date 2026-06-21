package com.pranjal.auth;

import com.pranjal.common.ApiResponse;
import com.pranjal.auth.dto.AuthResponse;
import com.pranjal.auth.dto.LoginRequest;
import com.pranjal.auth.dto.RegisterRequest;
import com.pranjal.auth.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication and authorization")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register the store owner (one-time only)")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.registerOwner(request.getName(), request.getEmail(),
                request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Owner registered successfully", response));
    }

    @Operation(summary = "Register the store owner (one-time only)")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Register the store owner (one-time only)")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMe(
            @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        UserProfileResponse response = authService.getMe(email);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
