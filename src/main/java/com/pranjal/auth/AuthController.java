package com.pranjal.auth;

import com.pranjal.common.ApiResponse;
import com.pranjal.auth.dto.AuthResponse;
import com.pranjal.auth.dto.LoginRequest;
import com.pranjal.auth.dto.RegisterRequest;
import com.pranjal.auth.dto.UserProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.registerOwner(request.getName(), request.getEmail(),
                request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Owner registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMe(
            @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        UserProfileResponse response = authService.getMe(email);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
