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
public class DailySalesResponse {
    private BigDecimal totalRevenue;
    private LocalDate date;
    private Long orderCount;
}
