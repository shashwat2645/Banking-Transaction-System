package com.banking.banking_transaction_system.service.impl;

import com.banking.banking_transaction_system.dto.request.CreateAccountRequest;
import com.banking.banking_transaction_system.dto.request.DepositRequest;
import com.banking.banking_transaction_system.dto.request.TransferRequest;
import com.banking.banking_transaction_system.dto.request.WithdrawRequest;
import com.banking.banking_transaction_system.dto.response.AccountResponse;
import com.banking.banking_transaction_system.dto.response.TransferResponse;
import com.banking.banking_transaction_system.entity.Account;
import com.banking.banking_transaction_system.entity.Customer;
import com.banking.banking_transaction_system.entity.IdempotencyRecord;
import com.banking.banking_transaction_system.enums.AccountStatus;
import com.banking.banking_transaction_system.enums.IdempotencyStatus;
import com.banking.banking_transaction_system.enums.TransactionStatus;
import com.banking.banking_transaction_system.enums.TransactionType;
import com.banking.banking_transaction_system.exception.*;
import com.banking.banking_transaction_system.repository.AccountRepository;
import com.banking.banking_transaction_system.repository.CustomerRepository;
import com.banking.banking_transaction_system.service.AccountService;
import com.banking.banking_transaction_system.service.IdempotencyService;
import com.banking.banking_transaction_system.service.TransactionService;
import com.banking.banking_transaction_system.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionService transactionService;
    private final IdempotencyService idempotencyService;

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
    @Transactional
    public AccountResponse deposit(DepositRequest request) {

        Account account = accountRepository.findByAccountNumberWithLock(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        account.setBalance(account.getBalance().add(request.getAmount()));

        Account updated = accountRepository.save(account);

        transactionService.recordTransaction(
                null,
                updated,
                request.getAmount(),
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCESS,
                "Cash deposit"
        );

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public AccountResponse withdraw(WithdrawRequest request) {

        Account account = accountRepository.findByAccountNumberWithLock(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));

        Account updated = accountRepository.save(account);

        transactionService.recordTransaction(
                updated,
                null,
                request.getAmount(),
                TransactionType.WITHDRAW,
                TransactionStatus.SUCCESS,
                "Cash withdrawal"
        );

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
    public TransferResponse transfer(String idempotencyKey, TransferRequest request) {

        // Validate basic constraints before taking DB locks
        if (request.getFromAccountNumber().equals(request.getToAccountNumber())) {
            throw new InvalidTransferException("Sender and receiver accounts cannot be the same.");
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException("Transfer amount must be greater than zero.");
        }

        String requestData = String.join(
                "|",
                request.getFromAccountNumber(),
                request.getToAccountNumber(),
                request.getAmount().toPlainString()
        );

        String requestHash = HashUtil.generateHash(requestData);

        Optional<IdempotencyRecord> existingRecord =
                idempotencyService.findByKey(idempotencyKey);

        if (existingRecord.isPresent()) {

            if (!existingRecord.get().getRequestHash().equals(requestHash)) {
                throw new IdempotencyKeyReuseException(
                        "Idempotency key already used for a different request."
                );
            }

            return TransferResponse.builder()
                    .message("Request already processed.")
                    .fromAccountNumber(request.getFromAccountNumber())
                    .toAccountNumber(request.getToAccountNumber())
                    .amount(request.getAmount())
                    .build();
        }

        // Deadlock Prevention: Deterministic Lock Acquisition Order
        String firstLockAcc = request.getFromAccountNumber().compareTo(request.getToAccountNumber()) < 0
                ? request.getFromAccountNumber()
                : request.getToAccountNumber();

        String secondLockAcc = request.getFromAccountNumber().compareTo(request.getToAccountNumber()) < 0
                ? request.getToAccountNumber()
                : request.getFromAccountNumber();

        Account firstLocked = accountRepository.findByAccountNumberWithLock(firstLockAcc)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + firstLockAcc));

        Account secondLocked = accountRepository.findByAccountNumberWithLock(secondLockAcc)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + secondLockAcc));

        Account sender = firstLocked.getAccountNumber().equals(request.getFromAccountNumber())
                ? firstLocked : secondLocked;
        Account receiver = firstLocked.getAccountNumber().equals(request.getToAccountNumber())
                ? firstLocked : secondLocked;

        // Check balance under lock
        if (sender.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance.");
        }

        sender.setBalance(sender.getBalance().subtract(request.getAmount()));
        receiver.setBalance(receiver.getBalance().add(request.getAmount()));

        Account updatedSender = accountRepository.save(sender);
        Account updatedReceiver = accountRepository.save(receiver);

        transactionService.recordTransaction(
                updatedSender,
                updatedReceiver,
                request.getAmount(),
                TransactionType.TRANSFER,
                TransactionStatus.SUCCESS,
                "Fund transfer"
        );

        TransferResponse response = TransferResponse.builder()
                .message("Transfer completed successfully.")
                .fromAccountNumber(sender.getAccountNumber())
                .toAccountNumber(receiver.getAccountNumber())
                .amount(request.getAmount())
                .build();

        IdempotencyRecord record = IdempotencyRecord.builder()
                .idempotencyKey(idempotencyKey)
                .endpoint("/api/accounts/transfer")
                .requestHash(requestHash)
                .response("Transfer completed successfully")
                .status(IdempotencyStatus.COMPLETED)
                .build();

        idempotencyService.save(record);

        return response;
    }
}