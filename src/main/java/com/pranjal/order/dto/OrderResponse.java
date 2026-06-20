package com.pranjal.order.dto;

import com.pranjal.order.DiscountType;
import com.pranjal.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private String orderNumber;
    private Long customerId;
    private Long createdBy;
    private OrderStatus orderStatus;
    private BigDecimal subtotal;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String notes;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
}
