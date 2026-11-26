package com.banking.banking_transaction_system.repository;

import com.banking.banking_transaction_system.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionId(String transactionId);

    boolean existsByTransactionId(String transactionId);
}
