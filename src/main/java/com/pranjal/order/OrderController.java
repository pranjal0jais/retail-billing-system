package com.pranjal.order;

import com.pranjal.common.ApiResponse;
import com.pranjal.order.dto.AddOrderItemRequest;
import com.pranjal.order.dto.ConfirmOrderRequest;
import com.pranjal.order.dto.CreateOrderRequest;
import com.pranjal.order.dto.OrderResponse;
import com.pranjal.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order lifecycle management")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create a new order in DRAFT state")
    @PostMapping()
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody @Valid CreateOrderRequest request,
                                                      @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order draft created successfully",
                        orderService.createOrder(request, userId)));
    }

    @Operation(summary = "Add an item to a DRAFT order")
    @PostMapping("/{orderId}/items")
    public ResponseEntity<ApiResponse<OrderResponse>> addItem(@RequestBody @Valid AddOrderItemRequest request,
                                                  @PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Item added successfully",
                        orderService.addItem(request, orderId)));
    }

    @Operation(summary = "Remove an item from a DRAFT order")
    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<ApiResponse<OrderResponse>> removeItem(@PathVariable Long orderId,
                                                     @PathVariable Long itemId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Item deleted successfully",
                        orderService.removeItem(itemId, orderId)));
    }

    @Operation(summary = "List all orders")
    @GetMapping()
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(orderService.getAllOrders(pageable)));
    }

    @Operation(summary = "Confirm an order — applies discount, deducts stock")
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<ApiResponse<OrderResponse>> confirmOrder(@PathVariable Long orderId,
                                                           @RequestBody(required = false)ConfirmOrderRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Order confirmed successfully",
                        orderService.confirmOrder(orderId, request)));
    }

    @Operation(summary = "Cancel an order — restores stock (Owner only)")
    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Order cancelled successfully",
                        orderService.cancelOrder(orderId)));
    }

    @Operation(summary = "Get full order details including line items")
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(orderService.getOrderById(orderId)));
    }
}
