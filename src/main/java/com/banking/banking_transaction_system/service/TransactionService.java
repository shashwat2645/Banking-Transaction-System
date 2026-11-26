package com.banking.banking_transaction_system.service;

import com.banking.banking_transaction_system.dto.response.TransactionResponse;
import com.banking.banking_transaction_system.entity.Account;
import com.banking.banking_transaction_system.enums.TransactionStatus;
import com.banking.banking_transaction_system.enums.TransactionType;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    TransactionResponse recordTransaction(
            Account fromAccount,
            Account toAccount,
            BigDecimal amount,
            TransactionType transactionType,
            TransactionStatus transactionStatus,
            String description
    );
    List<TransactionResponse> getAllTransactions();
}
