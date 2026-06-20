package com.pranjal.order.dto;

import com.pranjal.order.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmOrderRequest {
    private DiscountType discountType;
    private BigDecimal discountValue;
}