package com.pranjal.report;

import com.pranjal.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Sales and inventory reporting")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Get sales summary — revenue, order count, average order value for a date range")
    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/sales/summary")
    public ResponseEntity<ApiResponse<?>> getSalesSummary(@RequestParam LocalDate startDate,
                                                          @RequestParam LocalDate endDate) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(reportService
                        .getSalesSummary(startDate, endDate)));
    }

    @Operation(summary = "Get day-by-day sales breakdown for a date range")
    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/sales/daily")
    public ResponseEntity<ApiResponse<?>> getDailySales(@RequestParam LocalDate startDate,
                                                          @RequestParam LocalDate endDate) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(reportService
                        .getDailySales(startDate, endDate)));
    }

    @Operation(summary = "Get top N products by quantity sold in a date range")
    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/products/top-selling")
    public ResponseEntity<ApiResponse<?>> getTopsSellingProduct(@RequestParam LocalDate startDate,
                                                          @RequestParam LocalDate endDate,
                                                                @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(reportService
                        .getTopSellingProducts(startDate, endDate, limit)));
    }
}
