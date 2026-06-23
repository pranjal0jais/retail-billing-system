package com.pranjal.user;

import com.pranjal.common.ApiResponse;
import com.pranjal.user.dto.CreateStaffRequest;
import com.pranjal.user.dto.UpdatePasswordRequest;
import com.pranjal.user.dto.UpdateUserRequest;
import com.pranjal.user.dto.UserSummaryResponse;
import com.pranjal.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Staff account management")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Create a new staff account")
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> addStaff(@RequestBody @Valid CreateStaffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse
                        .success("New staff added successfully",
                                userService.createStaff(request)
                        )
                );
    }

    @Operation(summary = "List all staff accounts")
    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<List<UserSummaryResponse>>> getAllStaff() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse
                        .success(
                                userService.getAllStaff()
                        )
                );
    }

    @Operation(summary = "Get a staff account by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> getStaffById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse
                        .success(
                                userService.getStaffById(id)
                        )
                );
    }

    @Operation(summary = "Update a staff account by ID")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> updateUser(@RequestBody @Valid UpdateUserRequest request,
                                                     @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse
                        .success("Staff updated successfully",
                                userService.updateStaff(request, id)
                        )
                );
    }

    @Operation(summary = "Activate a staff account")
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> activateStaff(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse
                        .success("Staff activated successfully",
                                userService.activateStaff(id)
                        )
                );
    }

    @Operation(summary = "Deactivate a staff account")
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> deactivateStaff(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse
                        .success("Staff deactivated successfully",
                                userService.deactivateStaff(id)
                        )
                );
    }

    @Operation(summary = "Update own profile")
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> updateStaff(@RequestBody @Valid UpdateUserRequest request,
                                                      @AuthenticationPrincipal Jwt jwt) {
        Long id = jwt.getClaim("userId");
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse
                        .success("Account updated successfully",
                                userService.updateStaff(request, id)
                        )
                );
    }

    @Operation(summary = "Change own password")
    @PatchMapping("/me/password")
    @PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> changePassword(@RequestBody @Valid
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
