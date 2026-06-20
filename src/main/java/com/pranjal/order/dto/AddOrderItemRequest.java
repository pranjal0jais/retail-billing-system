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
    @NotNull
    private Long productId;
    @Min(1)
    @NotNull
    private Integer quantity;
}
