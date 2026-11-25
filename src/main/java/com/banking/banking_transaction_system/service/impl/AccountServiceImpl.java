package com.banking.banking_transaction_system.service.impl;

import com.banking.banking_transaction_system.dto.request.CreateAccountRequest;
import com.banking.banking_transaction_system.dto.request.DepositRequest;
import com.banking.banking_transaction_system.dto.request.TransferRequest;
import com.banking.banking_transaction_system.dto.request.WithdrawRequest;
import com.banking.banking_transaction_system.dto.response.AccountResponse;
import com.banking.banking_transaction_system.dto.response.TransferResponse;
import com.banking.banking_transaction_system.entity.Account;
import com.banking.banking_transaction_system.entity.Customer;
import com.banking.banking_transaction_system.enums.AccountStatus;
import com.banking.banking_transaction_system.exception.AccountNotFoundException;
import com.banking.banking_transaction_system.exception.CustomerNotFoundException;
import com.banking.banking_transaction_system.exception.InsufficientBalanceException;
import com.banking.banking_transaction_system.exception.InvalidTransferException;
import com.banking.banking_transaction_system.repository.AccountRepository;
import com.banking.banking_transaction_system.repository.CustomerRepository;
import com.banking.banking_transaction_system.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountServiceImpl(AccountRepository accountRepository,
                              CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public AccountResponse createAccount(CreateAccountRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .balance(request.getInitialDeposit())
                .status(AccountStatus.ACTIVE)
                .customer(customer)
                .build();

        Account savedAccount = accountRepository.save(account);

        return mapToResponse(savedAccount);
    }

    @Override
    public AccountResponse deposit(DepositRequest request) {

        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        account.setBalance(account.getBalance().add(request.getAmount()));

        Account updated = accountRepository.save(account);

        return mapToResponse(updated);
    }

    @Override
    public AccountResponse withdraw(WithdrawRequest request) {

        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));

        Account updated = accountRepository.save(account);

        return mapToResponse(updated);
    }

    @Override
    public AccountResponse getAccount(String accountNumber) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        return mapToResponse(account);
    }

    private AccountResponse mapToResponse(Account account) {

        return AccountResponse.builder()
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .status(account.getStatus())
                .build();
    }

    private String generateAccountNumber() {

        Random random = new Random();

        String accountNumber;

        do {
            accountNumber = String.valueOf(1000000000L + random.nextInt(900000000));
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    @Override
    @Transactional
    public TransferResponse transfer(TransferRequest request) {

        Account sender = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Sender account not found"));

        Account receiver = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Receiver account not found"));

        // Sender and receiver should not be the same
        if (sender.getAccountNumber().equals(receiver.getAccountNumber())) {
            throw new InvalidTransferException("Sender and receiver accounts cannot be the same.");
        }

        // Amount should be greater than zero
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException("Transfer amount must be greater than zero.");
        }

        // Sender should have sufficient balance
        if (sender.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance.");
        }

        sender.setBalance(sender.getBalance().subtract(request.getAmount()));

        receiver.setBalance(receiver.getBalance().add(request.getAmount()));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        return TransferResponse.builder()
                .message("Transfer completed successfully.")
                .fromAccountNumber(sender.getAccountNumber())
                .toAccountNumber(receiver.getAccountNumber())
                .amount(request.getAmount())
                .build();
    }
}