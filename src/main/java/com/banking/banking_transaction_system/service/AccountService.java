package com.banking.banking_transaction_system.service;

import com.banking.banking_transaction_system.dto.request.CreateAccountRequest;
import com.banking.banking_transaction_system.dto.request.DepositRequest;
import com.banking.banking_transaction_system.dto.request.WithdrawRequest;
import com.banking.banking_transaction_system.dto.response.AccountResponse;

public interface AccountService {

    AccountResponse createAccount(CreateAccountRequest request);

    AccountResponse deposit(DepositRequest request);

    AccountResponse withdraw(WithdrawRequest request);

    AccountResponse getAccount(String accountNumber);
}