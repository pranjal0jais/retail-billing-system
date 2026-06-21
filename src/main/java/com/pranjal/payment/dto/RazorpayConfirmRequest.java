package com.pranjal.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RazorpayConfirmRequest {
    @NotBlank(message = "Razorpay payment id cannot be null")
    private String razorpayPaymentId;

    @NotBlank(message = "Razorpay order id cannot be null")
    private String razorpayOrderId;

    @NotBlank(message = "Razorpay signature cannot be null")
    private String razorpaySignature;
}
