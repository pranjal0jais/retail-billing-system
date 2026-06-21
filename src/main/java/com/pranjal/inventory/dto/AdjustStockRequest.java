package com.pranjal.inventory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdjustStockRequest {
    @NotNull(message = "Product id cannot be null")
    private Long productId;

    @NotNull(message = "Quantity changed cannot be null")
    private Integer quantityChanged;

    private String note;
}
