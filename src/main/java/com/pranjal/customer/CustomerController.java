package com.pranjal.customer;

import com.pranjal.common.ApiResponse;
import com.pranjal.customer.dto.CreateCustomerRequest;
import com.pranjal.customer.dto.CustomerResponse;
import com.pranjal.customer.dto.UpdateCustomerRequest;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer profile management")
public class CustomerController {

    private final CustomerService customerService;
    private final OrderService orderService;

    @Operation(summary = "Create a new customer")
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(@RequestBody @Valid CreateCustomerRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer created successfully",
                        customerService.createCustomer(request)));
    }

    @Operation(summary = "List all customers")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CustomerResponse>>> getAllCustomer(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(customerService.getAllCustomer(pageable)));
    }

    @Operation(summary = "Search for a customer by phone number")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerByPhone(@RequestParam String phone) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(customerService.getCustomerByPhone(phone)));
    }

    @Operation(summary = "Create a new customer")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(customerService.getCustomerById(id)));
    }

    @Operation(summary = "Update a customer by ID")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(@RequestBody UpdateCustomerRequest request,
                                                                        @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Customer updated successfully",
                        customerService.updateCustomer(request, id)));
    }

    @Operation(summary = "Get order history for a customer")
    @GetMapping("/{id}/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getCustomerOrders(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(orderService.getOrderByCustomerId(id)));
    }

}
