package com.pranjal.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesSummaryResponse {
    private BigDecimal totalRevenue;
    private Long orderCount;
    private BigDecimal averageOrderValue;
    private LocalDate startDate;
    private LocalDate endDate;
}
