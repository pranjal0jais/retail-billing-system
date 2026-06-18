package com.pranjal.inventory;

import com.pranjal.common.ApiResponse;
import com.pranjal.inventory.dto.AdjustStockRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping("/adjust")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> adjustStock(@RequestBody @Valid AdjustStockRequest request,
                                                      @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Logged successfully",
                        inventoryService.adjustStock(request, userId)));
    }

    @GetMapping("/logs")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> getAllLogs(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(inventoryService.getLogs(pageable)));
    }

    @GetMapping("/logs/{productId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> getAllLogsByProduct(@PathVariable Long productId,
                                                              Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(inventoryService.getLogsByProductId(productId, pageable)));
    }

}
