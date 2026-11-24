package com.banking.banking_transaction_system.service;

import com.banking.banking_transaction_system.dto.request.CreateCustomerRequest;
import com.banking.banking_transaction_system.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(CreateCustomerRequest request);

    CustomerResponse getCustomerById(Long id);

    List<CustomerResponse> getAllCustomers();

    CustomerResponse updateCustomer(Long id, CreateCustomerRequest request);

    void deleteCustomer(Long id);
}