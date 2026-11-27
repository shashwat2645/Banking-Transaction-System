package com.banking.banking_transaction_system.controller;

import com.banking.banking_transaction_system.dto.request.CreateAccountRequest;
import com.banking.banking_transaction_system.dto.request.DepositRequest;
import com.banking.banking_transaction_system.dto.request.TransferRequest;
import com.banking.banking_transaction_system.dto.request.WithdrawRequest;
import com.banking.banking_transaction_system.dto.response.AccountResponse;
import com.banking.banking_transaction_system.dto.response.TransferResponse;
import com.banking.banking_transaction_system.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public AccountResponse createAccount(@RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request);
    }

    @GetMapping("/{accountNumber}")
    public AccountResponse getAccount(@PathVariable String accountNumber) {
        return accountService.getAccount(accountNumber);
    }

    @PostMapping("/deposit")
    public AccountResponse deposit(@RequestBody DepositRequest request) {
        return accountService.deposit(request);
    }

    @PostMapping("/withdraw")
    public AccountResponse withdraw(@RequestBody WithdrawRequest request) {
        return accountService.withdraw(request);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody TransferRequest request) {

        TransferResponse response =
                accountService.transfer(idempotencyKey, request);

        return ResponseEntity.ok(response);
    }
}