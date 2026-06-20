package com.pranjal.order;

import com.pranjal.common.ApiResponse;
import com.pranjal.order.dto.AddOrderItemRequest;
import com.pranjal.order.dto.ConfirmOrderRequest;
import com.pranjal.order.dto.CreateOrderRequest;
import com.pranjal.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping()
    public ResponseEntity<ApiResponse<?>> createOrder(@RequestBody @Valid CreateOrderRequest request,
                                                      @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order draft created successfully",
                        orderService.createOrder(request, userId)));
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<ApiResponse<?>> addItem(@RequestBody @Valid AddOrderItemRequest request,
                                                  @PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Item added successfully",
                        orderService.addItem(request, orderId)));
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<ApiResponse<?>> removeItem(@PathVariable Long orderId,
                                                     @PathVariable Long itemId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Item deleted successfully",
                        orderService.removeItem(itemId, orderId)));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<?>> getAllOrders() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(orderService.getAllOrders()));
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<ApiResponse<?>> confirmOrder(@PathVariable Long orderId,
                                                       @RequestBody(required = false)ConfirmOrderRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Order confirmed successfully",
                        orderService.confirmOrder(orderId, request)));
    }

    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Order cancelled successfully",
                        orderService.cancelOrder(orderId)));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<?>> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(orderService.getOrderById(orderId)));
    }
}
