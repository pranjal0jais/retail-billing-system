package com.pranjal.inventory;

import com.pranjal.common.ApiResponse;
import com.pranjal.inventory.dto.AdjustStockRequest;
import com.pranjal.inventory.dto.InventoryLogResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Manual stock adjustments and audit log")
public class InventoryController {
    private final InventoryService inventoryService;

    @Operation(summary = "Manually adjust stock for a product")
    @PostMapping("/adjust")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<InventoryLogResponse>> adjustStock(@RequestBody @Valid AdjustStockRequest request,
                                                      @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Logged successfully",
                        inventoryService.adjustStock(request, userId)));
    }

    @Operation(summary = "Get paginated inventory log, optionally filtered by product or date")
    @GetMapping("/logs")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Page<InventoryLogResponse>>> getAllLogs(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(inventoryService.getLogs(pageable)));
    }

    @Operation(summary = "Get stock movement history for a specific product")
    @GetMapping("/logs/{productId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Page<InventoryLogResponse>>> getAllLogsByProduct(@PathVariable Long productId,
                                                              Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(inventoryService.getLogsByProductId(productId, pageable)));
    }

}
