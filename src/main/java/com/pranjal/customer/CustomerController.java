package com.pranjal.customer;

import com.pranjal.common.ApiResponse;
import com.pranjal.customer.dto.CreateCustomerRequest;
import com.pranjal.customer.dto.UpdateCustomerRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createCustomer(@RequestBody @Valid CreateCustomerRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer created successfully",
                        customerService.createCustomer(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllCustomer() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(customerService.getAllCustomer()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> getCustomerByPhone(@RequestParam String phone) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(customerService.getCustomerByPhone(phone)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(customerService.getCustomerById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateCustomer(@RequestBody UpdateCustomerRequest request,
                                                         @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Customer updated successfully",
                        customerService.updateCustomer(request, id)));
    }
}
