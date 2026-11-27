package com.banking.banking_transaction_system.exception;

public class IdempotencyRecordNotFoundException extends RuntimeException {

    public IdempotencyRecordNotFoundException(String message) {
        super(message);
    }
}