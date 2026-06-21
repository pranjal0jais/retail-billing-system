package com.pranjal.report;

import com.pranjal.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/sales/summary")
    public ResponseEntity<ApiResponse<?>> getSalesSummary(@RequestParam LocalDate startDate,
                                                          @RequestParam LocalDate endDate) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(reportService
                        .getSalesSummary(startDate, endDate)));
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/sales/daily")
    public ResponseEntity<ApiResponse<?>> getDailySales(@RequestParam LocalDate startDate,
                                                          @RequestParam LocalDate endDate) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(reportService
                        .getDailySales(startDate, endDate)));
    }

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
