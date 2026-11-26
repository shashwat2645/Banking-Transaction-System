package com.banking.banking_transaction_system.service.impl;

import com.banking.banking_transaction_system.dto.response.TransactionResponse;
import com.banking.banking_transaction_system.entity.Account;
import com.banking.banking_transaction_system.entity.Transaction;
import com.banking.banking_transaction_system.enums.TransactionStatus;
import com.banking.banking_transaction_system.enums.TransactionType;
import com.banking.banking_transaction_system.repository.TransactionRepository;
import com.banking.banking_transaction_system.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private String generateTransactionId(){
        return UUID.randomUUID().toString();
    }

    @Override
    public TransactionResponse recordTransaction(Account fromAccount, Account toAccount, BigDecimal amount, TransactionType transactionType, TransactionStatus transactionStatus, String description) {
        Transaction transaction = Transaction.builder().fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(amount)
                .transactionType(transactionType)
                .transactionStatus(transactionStatus)
                .description(description)
                .transactionId(generateTransactionId())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToResponse(savedTransaction);
    }

    @Override
    public List<TransactionResponse> getAllTransactions() {

        return transactionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private TransactionResponse mapToResponse(Transaction transaction){
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .fromAccountNumber(transaction.getFromAccount()!=null ? transaction.getFromAccount().getAccountNumber() : null)
                .toAccountNumber(transaction.getToAccount()!=null ? transaction.getToAccount().getAccountNumber() : null)
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .transactionStatus(transaction.getTransactionStatus())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
