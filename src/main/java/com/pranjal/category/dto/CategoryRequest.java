package com.pranjal.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    @NotBlank(message = "Name cannot be blank")
    @Length(min = 2, max = 100, message = "Minimum 2 and Maximum 100 characters required for name")
    private String name;
    private String description;
}