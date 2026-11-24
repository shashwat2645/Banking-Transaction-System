package com.banking.banking_transaction_system.service.impl;

import com.banking.banking_transaction_system.dto.request.CreateCustomerRequest;
import com.banking.banking_transaction_system.dto.response.CustomerResponse;
import com.banking.banking_transaction_system.entity.Customer;
import com.banking.banking_transaction_system.repository.CustomerRepository;
import com.banking.banking_transaction_system.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponse createCustomer(CreateCustomerRequest request) {

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }

        Customer customer = Customer.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        return CustomerResponse.builder()
                .id(savedCustomer.getId())
                .fullName(savedCustomer.getFullName())
                .email(savedCustomer.getEmail())
                .phoneNumber(savedCustomer.getPhoneNumber())
                .address(savedCustomer.getAddress())
                .build();
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return CustomerResponse.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .address(customer.getAddress())
                .build();
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {

        return customerRepository.findAll()
                .stream()
                .map(customer -> CustomerResponse.builder()
                        .id(customer.getId())
                        .fullName(customer.getFullName())
                        .email(customer.getEmail())
                        .phoneNumber(customer.getPhoneNumber())
                        .address(customer.getAddress())
                        .build())
                .toList();
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CreateCustomerRequest request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setAddress(request.getAddress());

        Customer updatedCustomer = customerRepository.save(customer);

        return CustomerResponse.builder()
                .id(updatedCustomer.getId())
                .fullName(updatedCustomer.getFullName())
                .email(updatedCustomer.getEmail())
                .phoneNumber(updatedCustomer.getPhoneNumber())
                .address(updatedCustomer.getAddress())
                .build();
    }

    @Override
    public void deleteCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customerRepository.delete(customer);
    }
}