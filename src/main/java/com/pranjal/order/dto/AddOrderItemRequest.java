package com.pranjal.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddOrderItemRequest {
    @NotNull(message = "Product id cannot be null")
    private Long productId;

    @Min(value = 1, message = "Quantity should be at least 1")
    @NotNull(message = "Quantity should not be null")
    private Integer quantity;
}
