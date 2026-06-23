package com.pranjal.customer;

import com.pranjal.customer.dto.CreateCustomerRequest;
import com.pranjal.customer.dto.CustomerResponse;
import com.pranjal.customer.dto.UpdateCustomerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT
                    , "Customer already exists");
        }

        CustomerEntity customer = CustomerEntity.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .build();

        customer = customerRepository.save(customer);

        return toResponse(customer);
    }

    public CustomerResponse getCustomerByPhone(String phoneNumber) {
        CustomerEntity customer = customerRepository.findByPhone(phoneNumber)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        return toResponse(customer);
    }
    public CustomerResponse getCustomerById(Long id) {
        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        return toResponse(customer);
    }


    public Page<CustomerResponse> getAllCustomer(Pageable pageable) {
        return customerRepository.findAll(pageable).map(this::toResponse);
    }

    public CustomerResponse updateCustomer(UpdateCustomerRequest request, Long id) {
        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer not found"));

        if(customerRepository.existsByPhoneAndIdNot(request.getPhone(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot update customer with these data");
        }

        customer.setName(request.getName());
        customer.setPhone(request.getPhone());

        customerRepository.save(customer);

        return toResponse(customer);
    }



    private CustomerResponse toResponse(CustomerEntity entity) {
        return CustomerResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .phone(entity.getPhone())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
