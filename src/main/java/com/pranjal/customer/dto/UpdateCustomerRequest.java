package com.pranjal.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCustomerRequest {
    @NotBlank(message = "Name cannot be blank")
    @Length(min = 3, max = 100, message = "Name should be of 3 to 100 characters")
    private String name;


    @Pattern(regexp = "^\\d{10}$", message = "Invalid phone number")
    private String phone;
}
