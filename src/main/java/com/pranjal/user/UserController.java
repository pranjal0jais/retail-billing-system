package com.pranjal.user;

import com.pranjal.common.ApiResponse;
import com.pranjal.user.dto.CreateStaffRequest;
import com.pranjal.user.dto.UpdatePasswordRequest;
import com.pranjal.user.dto.UpdateUserRequest;
import com.pranjal.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> addStaff(@RequestBody @Valid CreateStaffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse
                        .success("New staff added successfully",
                                userService.createStaff(request)
                        )
                );
    }

    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> getAllStaff() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse
                        .success(
                                userService.getAllStaff()
                        )
                );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> getStaffById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse
                        .success(
                                userService.getStaffById(id)
                        )
                );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> updateUser(@RequestBody @Valid UpdateUserRequest request,
                                                     @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse
                        .success("Staff updated successfully",
                                userService.updateStaff(request, id)
                        )
                );
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> activateStaff(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse
                        .success("Staff activated successfully",
                                userService.activateStaff(id)
                        )
                );
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> deactivateStaff(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse
                        .success("Staff deactivated successfully",
                                userService.deactivateStaff(id)
                        )
                );
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
    public ResponseEntity<ApiResponse<?>> updateStaff(@RequestBody @Valid UpdateUserRequest request,
                                                      @AuthenticationPrincipal Jwt jwt) {
        Long id = jwt.getClaim("userId");
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse
                        .success("Account updated successfully",
                                userService.updateStaff(request, id)
                        )
                );
    }

    @PatchMapping("/me/password")
    @PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
    public ResponseEntity<ApiResponse<?>> changePassword(@RequestBody @Valid
                                                         UpdatePasswordRequest request,
                                                         @AuthenticationPrincipal Jwt jwt) {
        Long id = jwt.getClaim("userId");
        userService.changePassword(request, id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse
                        .success("Password changed successfully"
                        )
                );
    }
}
