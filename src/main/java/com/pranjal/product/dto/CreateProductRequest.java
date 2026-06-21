package com.pranjal.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    @NotBlank(message = "Name cannot be blank")
    @Length(min = 3, max = 100, message = "Name should be of 3 to 100 characters")
    private String name;

    @NotNull(message = "Price cannot be null")
    @Min(value = 1, message = "Price should be grater than 0")
    private BigDecimal price;

    @NotNull(message = "Stock quantity cannot be null")
    @Min(value = 1, message = "Stock quantity should be grater than 0")
    private Integer stockQuantity;

    @NotBlank(message = "SKU cannot be null")
    @Length(min = 3, max = 100, message = "SKU should be of 3 to 100 characters")
    private String sku;

    @NotNull(message = "Category id cannot be null")
    private Long categoryId;
}
