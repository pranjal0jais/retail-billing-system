package com.pranjal.payment;

import com.pranjal.common.ApiResponse;
import com.pranjal.payment.dto.PaymentResponse;
import com.pranjal.payment.dto.RazorpayConfirmRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment recording and Razorpay integration")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Record a cash payment for an order")
    @PostMapping("/orders/{orderId}/cash")
    public ResponseEntity<ApiResponse<PaymentResponse>> recordCashPayment(
            @PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cash payment recorded successfully",
                        paymentService.recordCashPayment(orderId)));
    }

    @Operation(summary = "Initiate a Razorpay payment and get the QR code URL")
    @PostMapping("/orders/{orderId}/razorpay/initiate")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiateRazorpayQr(
            @PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Razorpay QR initiated successfully",
                        paymentService.initializeRazorpayQr(orderId)));
    }

    @Operation(summary = "Confirm a Razorpay payment after QR scan verification")
    @PostMapping("/orders/{orderId}/razorpay/confirm")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmRazorpayPayment(
            @PathVariable Long orderId,
            @RequestBody @Valid RazorpayConfirmRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Payment confirmed successfully",
                        paymentService.confirmRazorpayPayment(orderId, request)));
    }

    @Operation(summary = "Get payment details by payment ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(paymentService.getPaymentById(id)));
    }

    @Operation(summary = "Get payment details by payment ID")
    @GetMapping("orders/{orderId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(paymentService.getPaymentByOrderId(orderId)));
    }
}
