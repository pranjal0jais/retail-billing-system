package com.pranjal.order.service;

import com.pranjal.customer.CustomerEntity;
import com.pranjal.customer.CustomerRepository;
import com.pranjal.inventory.ChangeType;
import com.pranjal.inventory.InventoryLogEntity;
import com.pranjal.inventory.InventoryRepository;
import com.pranjal.order.DiscountType;
import com.pranjal.order.OrderStatus;
import com.pranjal.order.dto.*;
import com.pranjal.order.entity.OrderEntity;
import com.pranjal.order.entity.OrderItemEntity;
import com.pranjal.order.repository.OrderItemRepository;
import com.pranjal.order.repository.OrderRepository;
import com.pranjal.product.ProductEntity;
import com.pranjal.product.ProductRepository;
import com.pranjal.user.UserEntity;
import com.pranjal.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Value("${app.order-number-prefix}")
    private String orderNumberPrefix;

    public OrderResponse createOrder(CreateOrderRequest request, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        CustomerEntity customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not " +
                                    "found"));
        }

        String orderNumber = generateOrderNumber();

        OrderEntity order = OrderEntity.builder()
                .orderNumber(orderNumber)
                .customer(customer)
                .createdBy(user)
                .notes(request.getNotes())
                .build();

        order = orderRepository.save(order);

        return toResponse(order);
    }

    public OrderResponse addItem(AddOrderItemRequest request, Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Order not found"));

        if (order.getOrderStatus() != OrderStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Order cannot be edited");
        }

        ProductEntity product = productRepository.findByIdAndIsActiveIsTrue(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product not found"));

        Optional<OrderItemEntity> existingItem = order.getItems()
                .stream()
                .filter(i -> i.getProduct().getId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            OrderItemEntity item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            if (product.getStockQuantity() < newQuantity) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Insufficient stock for product");
            }
            item.setQuantity(newQuantity);
            item.setLineTotal(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
        } else {
            if (product.getStockQuantity() < request.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Insufficient stock for product");
            }
            OrderItemEntity orderItem = OrderItemEntity.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .unitPrice(product.getPrice())
                    .quantity(request.getQuantity())
                    .lineTotal(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())))
                    .build();
            order.getItems().add(orderItem);
        }

        BigDecimal subTotal = order.getItems()
                .stream()
                .map(OrderItemEntity::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setSubTotal(subTotal);
        order.setTotalAmount(subTotal);
        orderRepository.save(order);
        return toResponse(order);
    }

    public OrderResponse removeItem(Long itemId, Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Order not found"));

        if (order.getOrderStatus() != OrderStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Order cannot be edited");
        }

        OrderItemEntity orderItem = orderItemRepository.findByIdAndOrder_Id(itemId, orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Order-item not found"));

        order.getItems().remove(orderItem);
        BigDecimal subTotal = order.getItems()
                .stream()
                .map(OrderItemEntity::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setSubTotal(subTotal);
        order.setTotalAmount(subTotal);
        orderRepository.save(order);
        return toResponse(order);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public OrderResponse confirmOrder(Long orderId, ConfirmOrderRequest request) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Order not found"));

        if (order.getOrderStatus() != OrderStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Order cannot be edited");
        }

        if (order.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Items cannot be empty");
        }

        Map<Long, ProductEntity> lockedProducts = new HashMap<>();
        for (OrderItemEntity i : order.getItems()) {
            ProductEntity p = productRepository.findByIdWithLock(i.getProduct().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Product not found"));
            if (p.getStockQuantity() < i.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Insufficient stock for product: " + i.getProductName());
            }
            lockedProducts.put(p.getId(), p);
        }

        if (request != null && request.getDiscountType() != null) {
            order.setDiscountType(request.getDiscountType());
            order.setDiscountValue(request.getDiscountValue());

            if (request.getDiscountType() == DiscountType.FLAT) {
                order.setDiscountAmount(request.getDiscountValue());
            } else {
                BigDecimal discountAmount = order.getSubTotal()
                        .multiply(request.getDiscountValue())
                        .divide(BigDecimal.valueOf(100));
                order.setDiscountAmount(discountAmount);
            }
        } else {
            order.setDiscountAmount(BigDecimal.ZERO);
        }

        order.setTotalAmount(order.getSubTotal().subtract(order.getDiscountAmount()));

        for (OrderItemEntity i : order.getItems()) {
            ProductEntity p = lockedProducts.get(i.getProduct().getId());
            p.setStockQuantity(p.getStockQuantity() - i.getQuantity());
            productRepository.save(p);

            InventoryLogEntity log = InventoryLogEntity.builder()
                    .product(p)
                    .changeType(ChangeType.SALE)
                    .quantityChanged(-i.getQuantity())
                    .quantityAfter(p.getStockQuantity())
                    .referenceId(orderId)
                    .note("Sale confirmed for order: " + order.getOrderNumber())
                    .createdBy(order.getCreatedBy().getId())
                    .build();
            inventoryRepository.save(log);
        }

        order.setOrderStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
        return toResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Order not found"));

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Already cancelled order cannot be cancelled");
        }

        if (order.getOrderStatus() == OrderStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Paid orders cannot be cancelled");
        }

        if (order.getOrderStatus() == OrderStatus.CONFIRMED) {
            for (OrderItemEntity i : order.getItems()) {
                ProductEntity p = productRepository.findById(i.getProduct().getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Product not found"));

                p.setStockQuantity(p.getStockQuantity() + i.getQuantity());
                productRepository.save(p);

                InventoryLogEntity log = InventoryLogEntity.builder()
                        .product(p)
                        .changeType(ChangeType.CANCELLATION)
                        .quantityChanged(i.getQuantity())
                        .quantityAfter(p.getStockQuantity())
                        .referenceId(orderId)
                        .note("cancelled for order: " + order.getOrderNumber())
                        .createdBy(order.getCreatedBy().getId())
                        .build();
                inventoryRepository.save(log);
            }
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return toResponse(order);
    }

    private OrderResponse toResponse(OrderEntity entity) {
        return OrderResponse.builder()
                .orderId(entity.getId())
                .orderNumber(entity.getOrderNumber())
                .customerId(entity.getCustomer() != null ? entity.getCustomer().getId() : null)
                .createdBy(entity.getCreatedBy().getId())
                .orderStatus(entity.getOrderStatus())
                .subtotal(entity.getSubTotal())
                .discountType(entity.getDiscountType())
                .discountValue(entity.getDiscountValue())
                .discountAmount(entity.getDiscountAmount())
                .totalAmount(entity.getTotalAmount())
                .notes(entity.getNotes())
                .items(entity.getItems()
                        .stream()
                        .map(this::toItemResponse)
                        .toList())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItemEntity entity) {
        return OrderItemResponse.builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .productName(entity.getProductName())
                .unitPrice(entity.getUnitPrice())
                .quantity(entity.getQuantity())
                .lineTotal(entity.getLineTotal())
                .build();
    }

    public OrderResponse getOrderById(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Order not found"));

        return toResponse(order);
    }

    private String generateOrderNumber() {
        int count = orderRepository.countAllByCreatedAtBetween(LocalDate.now().atStartOfDay(),
                LocalDate.now().atTime(LocalTime.MAX)) + 1;
        String postfix = String.format("%03d", count);
        String currentDate = LocalDate.now().toString();
        return orderNumberPrefix + "-" + currentDate + "-" + postfix;
    }

}
