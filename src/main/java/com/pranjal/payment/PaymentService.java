package com.pranjal.payment;

import com.pranjal.order.OrderStatus;
import com.pranjal.order.entity.OrderEntity;
import com.pranjal.order.repository.OrderRepository;
import com.pranjal.payment.dto.PaymentResponse;
import com.pranjal.payment.dto.RazorpayConfirmRequest;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final RazorpayClient razorpayClient;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Transactional
    public PaymentResponse recordCashPayment(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Order not found"));

        if (order.getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Order must be confirmed before payment");
        }

        if (paymentRepository.existsByOrder_Id(orderId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Payment record already exists");
        }

        PaymentEntity payment = PaymentEntity.builder()
                .order(order)
                .paymentMethod(PaymentMethod.CASH)
                .paymentStatus(PaymentStatus.COMPLETED)
                .amount(order.getTotalAmount())
                .paidAt(LocalDateTime.now())
                .build();

        payment = paymentRepository.save(payment);

        order.setOrderStatus(OrderStatus.PAID);

        orderRepository.save(order);

        return toResponse(payment);
    }

    private PaymentResponse toResponse(PaymentEntity entity) {
        return PaymentResponse.builder()
                .id(entity.getId())
                .orderId(entity.getOrder().getId())
                .paymentMethod(entity.getPaymentMethod())
                .paymentStatus(entity.getPaymentStatus())
                .amount(entity.getAmount())
                .razorpayOrderId(entity.getRazorpayOrderId())
                .razorpayPaymentId(entity.getRazorpayPaymentId())
                .paidAt(entity.getPaidAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Transactional
    public PaymentResponse initializeRazorpayQr(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Order not found"));

        if (order.getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Order must be confirmed before payment");
        }

        if (paymentRepository.existsByOrder_Id(orderId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Payment record already exists");
        }

        String razorpayOrderId = createRazorPayOrder(order.getTotalAmount(),
                order.getOrderNumber());

        PaymentEntity payment = PaymentEntity.builder()
                .order(order)
                .paymentMethod(PaymentMethod.ONLINE)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(order.getTotalAmount())
                .razorpayOrderId(razorpayOrderId)
                .build();

        paymentRepository.save(payment);
        return toResponse(payment);
    }

    @Transactional
    public PaymentResponse confirmRazorpayPayment(Long orderId, RazorpayConfirmRequest request) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Order not found"));

        PaymentEntity payment = paymentRepository.findByOrder_Id(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No payment initiated for this order"));

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Payment already completed");
        }

        String payload = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();
        String expectedSignature;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            expectedSignature = Hex.encodeHexString(hash);

            if (!expectedSignature.equals(request.getRazorpaySignature())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid payment signature");
            }
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Signature verification failed");
        }

        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setPaidAt(LocalDateTime.now());

        order.setOrderStatus(OrderStatus.PAID);

        paymentRepository.save(payment);
        orderRepository.save(order);

        return toResponse(payment);
    }

    public PaymentResponse getPaymentById(Long id) {
        PaymentEntity payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Payment not found"));
        return toResponse(payment);
    }

    private String createRazorPayOrder(BigDecimal amount, String orderNumber) {
        JSONObject object = new JSONObject();
        object.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
        object.put("currency", "INR");
        object.put("receipt", orderNumber);
        Order order = null;
        try {
            order = razorpayClient.orders.create(object);
        } catch (RazorpayException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Cannot process payment");
        }
        return order.get("id");
    }
}
